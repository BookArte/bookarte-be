package com.library.bookarte.borrow.repository;

import com.library.bookarte.borrow.dto.BorrowSearchFilterDto;
import com.library.bookarte.borrow.dto.response.MonthlyData;
import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.borrow.entity.type.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.library.bookarte.book.entity.QBook.book;
import static com.library.bookarte.borrow.entity.QBorrow.borrow;
import static com.library.bookarte.member.entity.QMember.member;

@RequiredArgsConstructor
@Repository
public class BorrowRepositoryCustomImpl implements BorrowRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Borrow> findAllBorrowByBorrowSearchFilter(BorrowSearchFilterDto borrowSearchFilterDto,
                                                          Pageable pageable){

        List<Borrow> content = jpaQueryFactory
                .selectFrom(borrow)
                .join(borrow.member, member).fetchJoin()
                .join(borrow.book, book).fetchJoin()
                .where(
                        statusEq(borrowSearchFilterDto.getStatus()),
                        isOverdueEq(borrowSearchFilterDto.getIsOverdue()),
                        memberIdEq(borrowSearchFilterDto.getMemberId())
                )
                .orderBy(borrow.borrowId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(borrow.count())
                .from(borrow)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // ===== 조건 메서드 =====

    // 상태값 조건 메서드
    private BooleanExpression statusEq(Status status) {
        return status != null ? borrow.status.eq(status) : null;
    }

    // 연장 여부에 따른 조건 메서드
    private BooleanExpression isOverdueEq(Boolean isOverdue) {
        return isOverdue != null ? borrow.isOverdue.eq(isOverdue) : null;
    }

    //회원 조건 메서드
    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? borrow.member.memberId.eq(memberId) : null;
    }

    @Override
    public List<MonthlyData> getRollingYearlyStatistics(Long bookId) {
        LocalDate today = LocalDate.now();
        LocalDate thisMonthFirstDay = today.withDayOfMonth(1);
        LocalDate oneYearAgoFirstDay = thisMonthFirstDay.minusYears(1);


        return jpaQueryFactory
                .select(Projections.constructor(
                        MonthlyData.class,
                        borrow.createdAt.year(),
                        borrow.createdAt.month(),
                        borrow.count()
                ))
                .from(borrow)
                .where(
                        bookIdEq(bookId),
                        borrow.createdAt.goe(oneYearAgoFirstDay.atStartOfDay()),
                        borrow.createdAt.lt(thisMonthFirstDay.atStartOfDay())

                )
                .groupBy(borrow.createdAt.year(),
                        borrow.createdAt.month())
                .orderBy(borrow.createdAt.year().asc(),
                        borrow.createdAt.month().asc())
                .fetch();
    }

    //도서 id 조건 메서드
    private BooleanExpression bookIdEq(Long bookId){
        return bookId != null ? borrow.book.bookId.eq(bookId) : null;
    }


}
