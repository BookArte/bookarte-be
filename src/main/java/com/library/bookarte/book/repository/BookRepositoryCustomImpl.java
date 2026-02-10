package com.library.bookarte.book.repository;

import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.library.bookarte.book.entity.QBook.book;
import static com.library.bookarte.book.entity.QBook_Participant.participant;
import static com.library.bookarte.category.entity.QCategory.category;
import static com.library.bookarte.borrow.entity.QBorrow.borrow;

@RequiredArgsConstructor
@Repository
@Slf4j
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<BookResDto> findBooks(SearchFilterDto searchFilterDto, Pageable pageable){

        //파라미터
        String categoryName = searchFilterDto.getCategory();
        String bookTitle = searchFilterDto.getBookTitle();
        String bookIsbn = searchFilterDto.getBookIsbn();
        String publisherName = searchFilterDto.getPublisherName();
        String author = searchFilterDto.getBookAuthor();
        LocalDate start = searchFilterDto.getPublicationDateStart();
        LocalDate end = searchFilterDto.getPublicationDateEnd();

        //조건 메서드들 분리
        BooleanExpression[] predicates = {
                categoryNameEq(categoryName),
                titleContains(bookTitle),
                isbnContains(bookIsbn),
                publisherContains(publisherName),
                authorContains(author),
                publicationDateBetween(start,end)
        };

        //도서 id만 선 조회
        List<Long> ids = jpaQueryFactory
                .select(book.bookId)
                .from(book)
                .join(book.category, category)
                .where(predicates)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 조회된 id들에 해당하는 데이터만 Fetch join으로 조회
        List<Book> books = jpaQueryFactory
                .selectFrom(book)
                .join(book.category, category).fetchJoin()
                .leftJoin(book.participants).fetchJoin()
                .where(book.bookId.in(ids))
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        List<BookResDto> content = books.stream()
                .map(Book::toBookResDto)
                .collect(Collectors.toList());


        // 전체 카운트 조회
        long total = jpaQueryFactory
                .select(book.count())
                .from(book)
                .where(predicates)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public  List<Book> findBooksAlsoBorrowed(Long bookId, Set<Long> excludeIds){
        List<Long> userIds = jpaQueryFactory
                .select(borrow.member.memberId)
                .from(borrow)
                .where(borrow.book.bookId.eq(bookId))
                .fetch();

        if(userIds.isEmpty()) return new ArrayList<>();

        return jpaQueryFactory
                .select(borrow.book)
                .from(borrow)
                .where(
                        borrow.member.memberId.in(userIds),
                        book.bookId.notIn(excludeIds)
                )
                .groupBy(borrow.book.bookId)
                .orderBy(borrow.count().desc())
                .limit(5)
                .fetch();
    }

    //같은 저자 대출수 순 조회
    @Override
    public List<Book> findBooksByAuthorOrderByBorrowCount(String authorName, Set<Long> excludeIds, int limit) {
        return jpaQueryFactory
                .select(book)
                .from(book)
                .leftJoin(borrow).on(borrow.book.eq(book))
                .join(book.participants, participant)
                .where(
                        authorContains(authorName),
                        book.bookId.notIn(excludeIds)
                )
                .groupBy(book.bookId)
                .orderBy(borrow.count().desc())
                .limit(limit)
                .fetch();
    }

    //같은 카테고리 대출수 순 조회
    @Override
    public List<Book> findBooksByCategoryOrderByBorrowCount(String category, Set<Long> excludeIds, int limit) {
        return jpaQueryFactory
                .select(book)
                .from(book)
                .leftJoin(borrow).on(borrow.book.eq(book))
                .where(
                        categoryNameEq(category),
                        book.bookId.notIn(excludeIds)
                )
                .groupBy(book.bookId)
                .orderBy(borrow.count().desc())
                .limit(limit)
                .fetch();
    }

    // ===== 조건 메서드 =====

    //카테고리 조건 메서드
    private BooleanExpression categoryNameEq(String categoryName) {
        return StringUtils.hasText(categoryName)
                ? book.category.categoryName.eq(categoryName)
                : null;
    }
    //도서 제목 조건 메서드
    private BooleanExpression titleContains(String bookTitle) {
        return StringUtils.hasText(bookTitle)
                ? book.bookTitle.contains(bookTitle)
                : null;
    }

    //isbn 조건 메서드
    private BooleanExpression isbnContains(String isbn) {
        return isbn != null
                ? book.bookIsbn.contains(isbn)
                : null;
    }

    //출판사 조건 메서드
    private BooleanExpression publisherContains(String publisherName) {
        return StringUtils.hasText(publisherName)
                ? book.publisherName.contains(publisherName)
                : null;
    }

    private BooleanExpression authorContains(String author){
        if(!StringUtils.hasText(author)) {
            return null;
        }
        return book.participants.any().name.contains(author)
                .and(book.participants.any().type.eq(ParticipantType.AUTHOR));
    }

    //특정 날짜 이후 출판일 조건 메서드 (publicationDate >= start)
    private BooleanExpression publicationDateGoe(LocalDate start) {
        return start != null ? book.publicationDate.goe(start) : null;
    }

    //특정 날짜 이전 출판일 조건 메서드 (publicationDate <= start)
    private BooleanExpression publicationDateLoe(LocalDate end) {
        return end != null ? book.publicationDate.loe(end) : null;
    }

    private BooleanExpression publicationDateBetween(LocalDate start, LocalDate end ){
        BooleanExpression isGoe = publicationDateGoe(start);
        BooleanExpression isLoe = publicationDateLoe(end);

        if (isGoe != null && isLoe != null) return isGoe.and(isLoe);
        if (isGoe != null) return isGoe;
        if (isLoe != null) return isLoe;

        return null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();

            switch (property) {
                case "bookTitle":
                    orders.add(new OrderSpecifier<>(direction, book.bookTitle));
                    break;
                case "bookAuthor":
                    // 저자가 다수가 있을 경우 첫번째 저자를 대표 저자로 하여 정렬
                    orders.add(new OrderSpecifier<>(direction, book.participants.any().name));
                    break;
                case "publicationDate":
                    orders.add(new OrderSpecifier<>(direction, book.publicationDate));
                    break;
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, book.createdAt));
                    break;
                default:
                    // 기본 정렬값 (최신 등록순)
                    orders.add(new OrderSpecifier<>(Order.DESC, book.createdAt));
                    break;
            }
        });

        return orders.toArray(new OrderSpecifier[0]);
    }

}
