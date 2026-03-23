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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
@ActiveProfiles("test")
public class BorrowServiceTest {

    @Autowired private BorrowService borrowService;
    @Autowired private BookRepository bookRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private EntityManager em;

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

    @Test
    @DisplayName("경계값 테스트: 반납 예정일이 어제인 도서만 연체 처리되어야 한다")
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
        System.out.println("연체 일수" + overdueTarget.getOverdueDays());

        // Then
        Borrow updatedOverdue = borrowRepository.findById(overdueTarget.getBorrowId()).orElseThrow();
        Borrow updatedNormal = borrowRepository.findById(normalTarget.getBorrowId()).orElseThrow();

        assertTrue(updatedOverdue.isOverdue(), "어제가 예정일인 도서는 연체 상태여야 함");
        assertEquals(1, updatedOverdue.getOverdueDays(), "연체 일수는 1일이어야 함");

        assertFalse(updatedNormal.isOverdue(), "오늘이 예정일인 도서는 아직 연체가 아니어야 함");
        assertEquals(0, updatedNormal.getOverdueDays(), "연체 일수는 0일이어야 함");
    }

}
