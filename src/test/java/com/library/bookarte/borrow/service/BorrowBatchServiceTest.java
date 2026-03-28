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
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringBatchTest
@Slf4j
@ActiveProfiles("test")
public class BorrowBatchServiceTest {

    @Autowired private BorrowService borrowService;
    @Autowired private BookRepository bookRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private EntityManager em;

    @Autowired private JobOperator jobOperator;
    @Autowired private Job overdueCheckJob;

    private Category savedCategory;

    @BeforeEach
    void setUp(){
        savedCategory = new Category("002","문학");
        categoryRepository.save(savedCategory);
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
        Borrow overdueTarget = FixtureFactory.createBorrow(member, book1, today.minusDays(1), Status.BORROWED);
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
