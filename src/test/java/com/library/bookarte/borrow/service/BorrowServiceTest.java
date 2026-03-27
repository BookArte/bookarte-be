package com.library.bookarte.borrow.service;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.borrow.entity.type.Status;
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
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringBatchTest
@Slf4j
@ActiveProfiles("test")
public class BorrowServiceTest {

    @Autowired private BorrowService borrowService;
    @Autowired private BookRepository bookRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private EntityManager em;

    @Autowired private JobOperator jobOperator;
    @Autowired private Job overdueCheckJob;

    private Long savedBookId;
    private List<Long> memberIds = new ArrayList<>();
    private final int testCount = 100;
    private Category savedCategory;

    @BeforeEach
    void setUp(){
        savedCategory = new Category("002","문학");
        categoryRepository.save(savedCategory);

        Book book = FixtureFactory.createBook("테스트",savedCategory);
        bookRepository.save(book);
        savedBookId = book.getBookId();

        for (int i = 1; i <= testCount; i++) {
            Member member = FixtureFactory.createMember("user" + i);
            memberRepository.save(member);
        }

        memberRepository.flush();

        memberIds = memberRepository.findAll().stream()
                .map(Member::getMemberId)
                .collect(Collectors.toList());

        em.clear();
    }

    @AfterEach
    void clean(){
        borrowRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        borrowRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동시 대출 테스트")
    void concurrentBorrowTest() throws InterruptedException {
        // Given: 대출 가능한 도서 1권 준비
        int threadCount = testCount;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

        // 성공과 실패 횟수를 기록하기 위한 카운터
        AtomicInteger successCount = new AtomicInteger(); //성공 카운터
        AtomicInteger failCount = new AtomicInteger(); // 실패 카운터

        long totalStartTime = System.currentTimeMillis(); // 전체 시작 시간

        // When: 100개의 스레드에서 동시에 borrowBook 호출
        for (int i = 0; i < threadCount; i++) {
            Long memberId = memberIds.get(i);
            executorService.execute(() -> {
                long taskStartTime = System.currentTimeMillis(); // 개별 작업 시작 시간
                try {
                    borrowService.borrowBook(savedBookId, memberId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    long taskEndTime = System.currentTimeMillis();
                    latencies.add(taskEndTime - taskStartTime); // 개별 응답 시간 기록
                    latch.countDown();
                }
            });
        }
        latch.await();
        long totalEndTime = System.currentTimeMillis();

        // Then: 성공은 무조건 1번, 실패는 99번이어야 함
        assertEquals(1, successCount.get(), "성공 횟수는 1이어야 합니다.");
        assertEquals(99, failCount.get(), "실패 횟수는 99이어야 합니다.");

        double averageLatency = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long maxLatency = latencies.stream().mapToLong(Long::longValue).max().orElse(0);

        // 최종 도서 상태 확인
        Book book = bookRepository.findById(savedBookId).orElseThrow();
        assertFalse(book.isCanBorrow(), "도서 상태는 대출 불가능(false)이어야 합니다.");
        System.out.println("=== 성능 테스트 결과 ===");
        System.out.println("전체 소요 시간: " + (totalEndTime - totalStartTime) + "ms");
        System.out.println("평균 응답 시간: " + averageLatency + "ms");
        System.out.println("최대 응답 시간(락 대기 포함): " + maxLatency + "ms");
    }

    @Test
    @DisplayName("트랜잭션 원자성 검증을 위한 의도적 예외 발생 테스트")
    void atomicityTest(){

        //Given
        Long bookId = savedBookId;
        Long memberId = memberIds.get(0);

        long initialBorrowCount = borrowRepository.count();
        Book bookBefore = bookRepository.findById(bookId).orElseThrow();
        assertTrue(bookBefore.isCanBorrow(), "처음에는 대출 가능 상태여야 함");


        //When
        assertThrows(RuntimeException.class, () -> {
            borrowService.borrowBookWithFailure(bookId, memberId);
        });

        //Then: 결과 검증
        long finalBorrowCount = borrowRepository.count();
        Book bookAfter = bookRepository.findById(bookId).orElseThrow();

        // 대출 이력이 생성되지 않았어야 함
        assertEquals(initialBorrowCount, finalBorrowCount, "에러 발생 시 대출 이력이 저장되면 안 됨");

        // 도서 상태가 여전히 '대출 가능(true)'이어야 함
        assertTrue(bookAfter.isCanBorrow(), "에러 발생 시 도서 상태가 false로 변하면 안 됨");
    }

    //기존 연체처리 로직에 대한 테스트
    @Test
    @Transactional
    @DisplayName("경계값 테스트: 날짜에 따른 연체 처리 여부 확인")
    void boundaryDateTest() {
        // Given
        LocalDate today = LocalDate.now();
        Book book1 = FixtureFactory.createBook("테스트1",savedCategory);
        Book book2 = FixtureFactory.createBook("테스트2",savedCategory);
        Member member = FixtureFactory.createMember("test");

        bookRepository.save(book1);
        bookRepository.save(book2);
        memberRepository.save(member);

        // 1. 연체 대상 (예정일: 어제)
        Borrow overdueTarget = FixtureFactory.createBorrow(member, book1, today.minusDays(1),Status.BORROWED);
        System.out.println("반납일: " + overdueTarget.getReturnDate());
        System.out.println("반납 예정일: " + overdueTarget.getReturnDueDate());
        // 2. 정상 대상 (예정일: 오늘)
        Borrow normalTarget = FixtureFactory.createBorrow(member, book2, today,Status.BORROWED);

        borrowRepository.saveAll(List.of(overdueTarget, normalTarget));

        // When
        borrowService.processOverdue();
        em.flush();
        em.clear();
        System.out.println("연체 일수" + overdueTarget.getOverdueDays());

        // Then
        Borrow updatedOverdue = borrowRepository.findById(overdueTarget.getBorrowId()).orElseThrow();
        Borrow updatedNormal = borrowRepository.findById(normalTarget.getBorrowId()).orElseThrow();

        assertTrue(updatedOverdue.isOverdue(), "어제가 예정일인 도서는 연체 상태여야 함");
        assertEquals(1, updatedOverdue.getOverdueDays(), "연체 일수는 1일이어야 함");

        assertFalse(updatedNormal.isOverdue(), "오늘이 예정일인 도서는 아직 연체가 아니어야 함");
        assertEquals(0, updatedNormal.getOverdueDays(), "연체 일수는 0일이어야 함");
    }

    @Test
    @DisplayName("멱등성 테스트: 동일한 연체 처리 대한 테스트")
    void idempotencyTest(){
        // Given
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);

        Book book1 = FixtureFactory.createBook("테스트1",savedCategory);
        bookRepository.save(book1);

        Member member = FixtureFactory.createMember("test");
        memberRepository.save(member);

        Borrow target = borrowRepository.save(FixtureFactory.createBorrow(member, book1, twoDaysAgo, Status.BORROWED));

        // When: 연체 처리 로직을 2번 연속 호출
        borrowService.processOverdue();
        int firstOverdueDays = borrowRepository.findById(target.getBorrowId()).get().getOverdueDays();

        borrowService.processOverdue();
        int secondOverdueDays = borrowRepository.findById(target.getBorrowId()).get().getOverdueDays();

        // Then: 연체 일수가 중복 합산되지 않고 동일해야 함
        assertEquals(2, firstOverdueDays);
        assertEquals(secondOverdueDays, firstOverdueDays, "여러 번 실행해도 연체 일수는 동일하게 유지되어야 함");
    }

    @Test
    @DisplayName("기존 코드 성능 테스트: 5,000건의 연체 데이터를 처리하는 소요 시간을 측정")
    void bulkPerformanceTest() {

        // Given
        prepareBulkData(5000);

        // When
        long startTime = System.currentTimeMillis();
        borrowService.processOverdue();
        long endTime = System.currentTimeMillis();

        // Then
        System.out.println("5,000건 처리 소요 시간: " + (endTime - startTime) + "ms");
    }

    @Test
    @DisplayName("경계값 테스트: 날짜에 따른 연체 처리 여부 확인")
    void boundaryDateBatchTest() throws Exception {
        // Given
        LocalDate today = LocalDate.now();
        Book book1 = FixtureFactory.createBook("테스트1",savedCategory);
        Book book2 = FixtureFactory.createBook("테스트2",savedCategory);
        Member member = FixtureFactory.createMember("test");

        bookRepository.save(book1);
        bookRepository.save(book2);
        memberRepository.save(member);

        Borrow overdueTarget = FixtureFactory.createBorrow(member, book1, today.minusDays(1),Status.BORROWED);
        Borrow normalTarget = FixtureFactory.createBorrow(member, book2, today,Status.BORROWED);

        borrowRepository.saveAll(List.of(overdueTarget, normalTarget));


        // When: today 파라미터를 2026-03-25로 고정하여 실행
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addString("today", LocalDate.now().toString())
                .toJobParameters();

        JobExecution execution = jobOperator.start(overdueCheckJob, params); // JobExplorer 주입 필요
        executionLogging(execution);

        // Then
        Borrow updatedOverdue = borrowRepository.findById(overdueTarget.getBorrowId()).orElseThrow();
        Borrow updatedNormal = borrowRepository.findById(normalTarget.getBorrowId()).orElseThrow();

        assertTrue(updatedOverdue.isOverdue(), "어제가 예정일인 도서는 연체 상태여야 함");
        assertEquals(1, updatedOverdue.getOverdueDays(), "연체 일수는 1일이어야 함");

        assertFalse(updatedNormal.isOverdue(), "오늘이 예정일인 도서는 아직 연체가 아니어야 함");
        assertEquals(0, updatedNormal.getOverdueDays(), "연체 일수는 0일이어야 함");
    }

    @Test
    @DisplayName("멱등성 테스트: 동일한 연체 처리에 대한 테스트")
    void idempotencyBatchTest() throws Exception {
        // Given
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);

        Book book1 = FixtureFactory.createBook("테스트1", savedCategory);
        bookRepository.save(book1);

        Member member = FixtureFactory.createMember("test");
        memberRepository.save(member);

        Borrow target = borrowRepository.save(FixtureFactory.createBorrow(member, book1, twoDaysAgo, Status.BORROWED));

        // When: 1차 실행
        JobParameters params1 = new JobParametersBuilder()
                .addLong("run.id", 1L) // 고유한 run.id 사용
                .addString("today", LocalDate.now().toString())
                .toJobParameters();

        JobExecution execution1 =  jobOperator.start(overdueCheckJob, params1);
        executionLogging(execution1);

        int firstOverdueDays = borrowRepository.findById(target.getBorrowId()).get().getOverdueDays();

        // 2차 실행 (동일한 run.id 사용)
        JobParameters params2 = new JobParametersBuilder()
                .addLong("run.id", 2L) // 고유한 run.id 사용
                .addString("today", LocalDate.now().toString())
                .toJobParameters();

        JobExecution execution2 =  jobOperator.start(overdueCheckJob, params2);
        executionLogging(execution2);

        int secondOverdueDays = borrowRepository.findById(target.getBorrowId()).get().getOverdueDays();

        // Then: 연체 일수가 중복 합산되지 않고 동일해야 함
        assertEquals(2, firstOverdueDays);
        assertEquals(secondOverdueDays, firstOverdueDays, "여러 번 실행해도 연체 일수는 동일하게 유지되어야 함");
    }


    @Test
    @DisplayName("최신 표준 성능 테스트: 5,000건의 연체 데이터를 처리하는 소요 시간을 측정")
    void bulkBatchPerformanceTest() throws Exception {

        //Given
        prepareBulkData(5000);

        //When
        long startTime = System.currentTimeMillis();
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addString("today", LocalDate.now().toString())
                .toJobParameters();

        JobExecution execution = jobOperator.start(overdueCheckJob, params);
        executionLogging(execution);
        long endTime = System.currentTimeMillis();

        //Then
        System.out.println("5,000건 처리 소요 시간: " + (endTime - startTime) + "ms");
    }

    private void prepareBulkData(int count) {
        // 데이터 생성 로직 (생략)
        List<Borrow> bulkBorrows = new ArrayList<>();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        Book book1 = FixtureFactory.createBook("테스트1",savedCategory);
        bookRepository.save(book1);

        Member member = FixtureFactory.createMember("test");
        memberRepository.save(member);

        for (int i = 0; i < count; i++) {
            bulkBorrows.add(FixtureFactory.createBorrow(member, book1, yesterday, Status.BORROWED));
        }
        borrowRepository.saveAll(bulkBorrows);
    }

    private void executionLogging(JobExecution execution){
        log.info("배치 완료 상태: {}", execution.getStatus());
        log.info("읽은 데이터 수: {}", execution.getStepExecutions().iterator().next().getReadCount());
        log.info("쓴 데이터 수: {}", execution.getStepExecutions().iterator().next().getWriteCount());
    }
}
