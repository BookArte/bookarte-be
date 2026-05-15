package com.library.bookarte.borrow.repository;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.borrow.dto.BookIdCountDto;
import com.library.bookarte.borrow.dto.BorrowSearchFilterDto;
import com.library.bookarte.borrow.dto.response.MonthlyData;
import com.library.bookarte.borrow.dto.response.PopularBookResDto;
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
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


        BooleanExpression[] predicates = {
                statusEq(borrowSearchFilterDto.getStatus()),
                statusNotEq(borrowSearchFilterDto.getStatusNot()),
                isOverdueEq(borrowSearchFilterDto.getIsOverdue()),
                memberIdEq(borrowSearchFilterDto.getMemberId()),
                titleContains(borrowSearchFilterDto.getSearchKeyword())
        };

        List<Borrow> content = jpaQueryFactory
                .selectFrom(borrow)
                .join(borrow.member, member).fetchJoin()
                .join(borrow.book, book).fetchJoin()
                .where(predicates)
                .orderBy(borrow.borrowId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        long total = jpaQueryFactory
                .select(borrow.count())
                .from(borrow)
                .where(predicates)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
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


    @Override
    public Page<PopularBookResDto> findPopularBooks(String period, Pageable pageable){
        LocalDateTime now =  LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(period, now);

        List<BookIdCountDto> stats = jpaQueryFactory
                .select(Projections.constructor(BookIdCountDto.class,
                        borrow.book.bookId,
                        borrow.count()
                ))
                .from(borrow)
                .where(borrow.createdAt.after(startDate))
                .groupBy(borrow.book.bookId)
                .orderBy(borrow.count().desc(), borrow.book.bookId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if(stats.isEmpty()){
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Long> bookIds = stats.stream().map(BookIdCountDto::getBookId).toList();

        Map<Long, Long> countMap = stats.stream()
                .collect(Collectors.toMap(BookIdCountDto::getBookId,BookIdCountDto::getBorrowCount));

        List<Book> books = jpaQueryFactory
                .selectFrom(book)
                .leftJoin(book.participants).fetchJoin()
                .where(book.bookId.in(bookIds))
                .fetch();

        List<PopularBookResDto> content = books.stream()
                .map(book -> {
                    Long borrowCount = countMap.getOrDefault(book.getBookId(), 0L);
                    return book.toPopularBookResDto(borrowCount);
                })
                .sorted(Comparator.comparing(PopularBookResDto::getBorrowCount).reversed())
                .toList();

        long total = jpaQueryFactory
                .select(borrow.book.bookId.countDistinct())
                .from(borrow)
                .where(book.createdAt.after(startDate))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private LocalDateTime calculateStartDate(String period, LocalDateTime now){
        return switch (period) {
            case "MONTH" -> now.minusMonths(1);
            case "YEAR" -> now.minusYears(1);
            default -> now.minusWeeks(1);
        };
    }

    @Override
    public List<PopularBookResDto> findTopPopularBooks(String period, int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(period, now);

        // 1. 통계 데이터 집계 (Top N)
        List<BookIdCountDto> stats = jpaQueryFactory
                .select(Projections.constructor(BookIdCountDto.class,
                        borrow.book.bookId,
                        borrow.count()
                ))
                .from(borrow)
                .where(borrow.createdAt.after(startDate))
                .groupBy(borrow.book.bookId)
                .orderBy(borrow.count().desc(), borrow.book.bookId.asc())
                .limit(limit)
                .fetch();

        if (stats.isEmpty()) return Collections.emptyList();

        List<Long> bookIds = stats.stream().map(BookIdCountDto::getBookId).toList();
        Map<Long, Long> countMap = stats.stream()
                .collect(Collectors.toMap(BookIdCountDto::getBookId, BookIdCountDto::getBorrowCount));

        // 2. 상세 정보 조회 (In-query 및 Fetch Join)
        List<Book> books = jpaQueryFactory
                .selectFrom(book)
                .leftJoin(book.participants).fetchJoin()
                .where(book.bookId.in(bookIds))
                .fetch();

        // 3. 결과 매핑 및 정렬
        return books.stream()
                .map(book -> book.toPopularBookResDto(countMap.getOrDefault(book.getBookId(), 0L)))
                .sorted(Comparator.comparing(PopularBookResDto::getBorrowCount).reversed())
                .toList();
    }


    // ===== 조건 메서드 =====

    // 상태값 조건 메서드
    private BooleanExpression statusEq(Status status) {
        return status != null ? borrow.status.eq(status) : null;
    }

    private BooleanExpression statusNotEq(Status status) { return status != null ? borrow.status.ne(status) : null; }

    // 연장 여부에 따른 조건 메서드
    private BooleanExpression isOverdueEq(Boolean isOverdue) {
        return isOverdue != null ? borrow.isOverdue.eq(isOverdue) : null;
    }

    //회원 조건 메서드
    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? borrow.member.memberId.eq(memberId) : null;
    }

    //도서 id 조건 메서드
    private BooleanExpression bookIdEq(Long bookId){
        return bookId != null ? borrow.book.bookId.eq(bookId) : null;
    }

    private BooleanExpression titleContains(String searchKeyword) {
        return StringUtils.hasText(searchKeyword)
                ? borrow.book.bookTitle.contains(searchKeyword)
                : null;
    }
}
