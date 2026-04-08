package com.library.bookarte.borrow.service;


import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.borrow.dto.response.MonthlyData;
import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.borrow.entity.stats.BookMonthlyStats;
import com.library.bookarte.borrow.entity.type.Status;
import com.library.bookarte.borrow.repository.BookMonthlyStatsRepository;
import com.library.bookarte.borrow.repository.BorrowRepository;
import com.library.bookarte.category.entity.Category;
import com.library.bookarte.category.reposiotry.CategoryRepository;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import com.library.bookarte.support.FixtureFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@SpringBatchTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
@ActiveProfiles("test")
public class BookStatServiceTest {

    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private BorrowService borrowService;
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private EntityManager em;
    @Autowired private RedisTemplate<String, Object> redisTemplate;
    @MockitoSpyBean
    private BookMonthlyStatsRepository bookMonthlyStatsRepository;

    @Autowired private JobOperator jobOperator;
    @Autowired private Job bookMonthlyStatJob;

    private Long savedBookId;
    private Long savedMemberId;

    @BeforeEach
    void setUp(){
        transactionTemplate.execute(status -> {
            Category savedCategory = categoryRepository.save(new Category("002","문학"));
            Member member = memberRepository.save(FixtureFactory.createMember("user"));
            savedMemberId = member.getMemberId();

            Book book = bookRepository.save(FixtureFactory.createBook("테스트", savedCategory));
            savedBookId = book.getBookId();

            for (int i = 1 ; i  < 11; i++ ){
                Book dummyBook = bookRepository.save(FixtureFactory.createBook("테스트" + i, savedCategory));
                createMonhtlyDummyBorrows(dummyBook.getBookId());
            }


            return null;
        });
        em.clear();
    }

    @AfterEach
    void clean(){
        borrowRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        borrowRepository.deleteAllInBatch();
        bookMonthlyStatsRepository.deleteAllInBatch();
        redisTemplate.delete("book:stats:" + savedBookId);
    }

    @Test
    @Transactional
    @DisplayName("기존 코드: 캐싱 없이 실시간으로  12개월치 대출 통계 집계하여 조회")
    void getRollingYearHistory_Legarcy(){
        saveBorrowWithDate(savedBookId, LocalDateTime.now().minusMonths(1));
        saveBorrowWithDate(savedBookId, LocalDateTime.now().minusMonths(1));
        saveBorrowWithDate(savedBookId, LocalDateTime.now().minusMonths(2));
        // when
        List<Borrow> borrows = borrowRepository.findAll();
        for(int i = 0; i < borrows.size(); i++){
            log.info("대출 더미 목록: {}", borrows.get(i).getCreatedAt());
        }

        List<MonthlyData> result = borrowService.getRollingYearHistory(savedBookId);

        assertThat(result).hasSize(12);
        log.info("리스트: {}",result);

        MonthlyData lastMonth = result.get(result.size() - 2);
        log.info("지난 달: {}",lastMonth);
        assertThat(lastMonth.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("신규 코드: 대출 통계에 대한 사전집계 테스트")
    void getRollingYearHistory_New() throws Exception {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        transactionTemplate.execute(status -> {
            for (int i = 0 ; i < 3; i++){
                saveBorrowWithDate(savedBookId, yesterday);
            }
           return null;
        });


        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addString("today", LocalDate.now().toString())
                .toJobParameters();

        JobExecution jobExecution = jobOperator.start(bookMonthlyStatJob,params);

        while (jobExecution.isRunning()) {
            Thread.sleep(200);
        }

        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            throw new IllegalStateException("배치 작업 실패: " + jobExecution.getStatus());
        }

        Optional<BookMonthlyStats> stats = bookMonthlyStatsRepository.findByBookIdAndStatYearAndStatMonth(
                savedBookId, yesterday.getYear(), yesterday.getMonthValue());

        assertThat(stats.isPresent()).isTrue();

        assertThat(stats.get().getBorrowCount()).isEqualTo(3L);

        String cacheKey = "book:stats:" + savedBookId;
        redisTemplate.delete(cacheKey); // 테스트 전 캐시 비우기

        List<MonthlyData> firstResult = borrowService.getRollingYearHistoryWithCache(savedBookId);

        assertThat(firstResult).isNotEmpty();

        // 실제 DB를 조회했는지 확인 (SpyBean 사용 시)
        verify(bookMonthlyStatsRepository, times(1)).findLastYearStats(anyLong(), anyInt(), anyInt());
        // Redis에 값이 들어갔는지 확인
        assertThat(redisTemplate.hasKey(cacheKey)).isTrue();

        // 두 번째 조회: Cache Hit 발생 -> DB 조회 없이 Redis 반환
        List<MonthlyData> secondResult = borrowService.getRollingYearHistoryWithCache(savedBookId);

        assertThat(secondResult).isEqualTo(firstResult);
        // DB 조회 횟수가 여전히 1이어야 함 (두 번째는 호출 안 함)
        verify(bookMonthlyStatsRepository, times(1)).findLastYearStats(anyLong(), anyInt(), anyInt());

    }

    @Test
    @DisplayName("신규 코드: 배치 집계 후 캐싱된 12개월 통계 조회 확인")
    void getRollingYearHistroy_New_Inergration() throws Exception {

        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        bookMonthlyStatsRepository.save(BookMonthlyStats.builder()
                .bookId(savedBookId)
                .statYear(lastMonth.getYear())
                .statMonth(lastMonth.getMonthValue())
                .borrowCount(2L)
                .build());

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        transactionTemplate.execute(status -> {
            saveBorrowWithDate(savedBookId, yesterday);
            return null;
        });

        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addString("today", LocalDate.now().toString())
                .toJobParameters();

        JobExecution jobExecution = jobOperator.start(bookMonthlyStatJob,params);

        String cacheKey = "book:stats:" + savedBookId;
        redisTemplate.delete(cacheKey);

        List<MonthlyData> result = borrowService.getRollingYearHistoryWithCache(savedBookId);
        assertThat(result).hasSize(12);

        MonthlyData lastMonthResult = result.stream()
                .filter(d -> d.year() == lastMonth.getYear() && d.month() == lastMonth.getMonthValue())
                .findFirst().orElseThrow();
        assertThat(lastMonthResult.count()).isEqualTo(2L);

        log.info("조회된 통계 리스트: {}", result);

        verify(bookMonthlyStatsRepository, times(1)).findLastYearStats(anyLong(), anyInt(), anyInt());
        assertThat(redisTemplate.hasKey(cacheKey)).isTrue();
    }

    @Test
    @DisplayName("다중 사용자 동시 조회 시 성능 비교 테스트")
    void concurrentLoadTest() throws InterruptedException {
        int threadCount = 30; // 동시 접속자 수
        int totalRequests = 300; // 총 요청 횟수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(totalRequests);

        // 측정 시작
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            final Long bookId = (long) (new Random().nextInt(10) + 1); // 1~10번 도서 랜덤 조회
            executorService.execute(() -> {
                try {
                    // 기존 로직과 고도화 로직에 대한 성능 비교
                    borrowService.getRollingYearHistoryWithCache(bookId); // 고도화 로직
                    /*borrowService.getRollingYearHistory(bookId);*/ //기존 로직
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 종료될 때까지 대기
        long endTime = System.currentTimeMillis();

        System.out.println("========================================");
        System.out.println("총 소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("요청당 평균 시간: " + (double)(endTime - startTime) / totalRequests + "ms");
        System.out.println("========================================");

        executorService.shutdown();
    }

    private void createMonhtlyDummyBorrows(Long bookId){
        // 1. 최근 15개월치 데이터를 생성하여 경계값(12개월) 테스트 준비
        for (int monthOffset = 0; monthOffset < 15; monthOffset++) {
            // 2. 특정 달(예: 3개월 전)은 데이터를 넣지 않아 '0' 카운트 패딩 테스트
            if (monthOffset == 3) continue;

            // 3. 월별로 1~5건의 랜덤한 대출 생성
            int dailyCount = new Random().nextInt(5) + 1;
            for (int i = 0; i < dailyCount; i++) {
                LocalDateTime pastDate = LocalDateTime.now()
                        .minusMonths(monthOffset)
                        .withDayOfMonth(new Random().nextInt(28) + 1);

                saveBorrowWithDate(bookId, pastDate);
            }
        }
    }


    private void saveBorrowWithDate(Long bookId, LocalDateTime date){
        Book book = bookRepository.getReferenceById(bookId);
        Member member = memberRepository.getReferenceById(savedMemberId);

        Borrow borrow = Borrow.builder()
                .book(book)
                .member(member)
                .status(Status.RETURNED)
                .build();

        borrowRepository.saveAndFlush(borrow);


        //JPQL 업데이트 쿼리를 사용하여 Auditing을 우회합니다.
        em.createQuery("UPDATE Borrow b SET b.createdAt = :date WHERE b.borrowId = :id")
                .setParameter("date", date)
                .setParameter("id", borrow.getBorrowId())
                .executeUpdate();

        em.clear();
    }
}
