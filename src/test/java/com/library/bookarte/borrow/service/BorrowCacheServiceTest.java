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
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@SpringBatchTest
@Slf4j
@ActiveProfiles("test")
public class BorrowCacheServiceTest {

    @Autowired private BorrowService borrowService;
    @Autowired private BookRepository bookRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private EntityManager em;

    private Category savedCategory;

    @BeforeEach
    void setUp(){
        savedCategory = new Category("002","문학");
        categoryRepository.save(savedCategory);
        prepareBulkData(50000);
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
    @DisplayName("기존코드(No Cache):  인기 도서 조회 성능 측정")
    void measureGetPopularBooksPerformance(){
        StopWatch stopWatch = new StopWatch("인기 도서 조회 성능 테스트");

        // 1. 첫 번째 실행 (Cold Start: DB 인덱스나 캐시가 전혀 없는 상태)
        stopWatch.start("First Request (Cold)");
        borrowService.getPopularBooks("WEEK", PageRequest.of(0, 10));
        stopWatch.stop();

        // 2. 반복 실행 (평균 성능 측정)
        for (int i = 1; i <= 5; i++) {
            stopWatch.start("Repeat Request - " + i);
            borrowService.getPopularBooks("WEEK", PageRequest.of(0, 10));
            stopWatch.stop();
        }

        // 결과 출력
        log.info(stopWatch.prettyPrint());
        log.info("평균 소요 시간: {}ms",(stopWatch.getTotalTimeMillis() / 6.0));

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


}
