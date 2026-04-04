package com.library.bookarte.borrow.service;


import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.borrow.dto.response.MonthlyData;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@SpringBatchTest
@Slf4j
@ActiveProfiles("test")
public class BookStatServiceTest {
    @Autowired private BorrowService borrowService;
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private EntityManager em;

    private Long savedBookId;
    private Long savedMemberId;

    @BeforeEach
    void setUp(){
        Category savedCategory = new Category("002","문학");
        categoryRepository.save(savedCategory);

        Book book = FixtureFactory.createBook("테스트",savedCategory);
        bookRepository.save(book);
        savedBookId = book.getBookId();

        Member member = FixtureFactory.createMember("user");
        memberRepository.save(member);
        memberRepository.flush();

        savedMemberId = member.getMemberId();


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
    @Transactional
    @DisplayName("기존 코드: 캐싱 없이 실시간으로  12개월치 대출 통계 집계하여 조회")
    void getRollingYearHistory_Legarcy(){
        saveBorrow(savedBookId, LocalDateTime.now().minusMonths(1));
        saveBorrow(savedBookId, LocalDateTime.now().minusMonths(1));
        saveBorrow(savedBookId, LocalDateTime.now().minusMonths(2));
        // when
        List<Borrow> borrows = borrowRepository.findAll();
        for(int i = 0; i < borrows.size(); i++){
            log.info("대출 더미 목록: {}", borrows.get(i).getCreatedAt());
        }

        List<MonthlyData> result = borrowService.getRollingYearHistory(savedBookId);

        // then: 리스트 사이즈는 12여야 함
        assertThat(result).hasSize(12);
        log.info("리스트: {}",result);

        MonthlyData lastMonth = result.get(result.size() - 2);
        log.info("지난 달: {}",lastMonth);
        assertThat(lastMonth.count()).isEqualTo(1L);
    }

    private void saveBorrow(Long bookId, LocalDateTime date){
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

        // 영속성 컨텍스트 초기화 (DB의 변경사항을 다음 조회 때 반영하기 위함)
        em.clear();
    }


}
