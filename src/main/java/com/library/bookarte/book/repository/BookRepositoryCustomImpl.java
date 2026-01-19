package com.library.bookarte.book.repository;

import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.group.GroupBy.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.library.bookarte.book.entity.QBook.book;
import static com.library.bookarte.category.entity.QCategory.category;

@RequiredArgsConstructor
@Repository
@Slf4j
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<BookResDto> findBooks(SearchFilterDto searchFilterDto, Pageable pageable){
        String categoryName = searchFilterDto.getCategory();
        String bookTitle = searchFilterDto.getBookTitle();
        String bookIsbn = searchFilterDto.getIsbn();
        String publisherName = searchFilterDto.getPublisherName();
        String author = searchFilterDto.getAuthor();
        LocalDate start = searchFilterDto.getPublicationDateStart();
        LocalDate end = searchFilterDto.getPublicationDateEnd();

        //도서 id만 선 조회
        List<Long> ids = jpaQueryFactory
                .select(book.bookId)
                .from(book)
                .join(book.category, category)
                .where(
                        categoryNameEq(categoryName),
                        titleContains(bookTitle),
                        isbnContains(bookIsbn),
                        publisherContains(publisherName),
                        authorContains(author),
                        publicationDateBetween(start,end)
                )
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
                .leftJoin(book.participants).fetchJoin() // 컬렉션 페치 조인
                .where(book.bookId.in(ids))
                .fetch();

        List<BookResDto> content = books.stream()
                .map(Book::toBookResDto)
                .collect(Collectors.toList());


        // 전체 카운트 조회
        long total = jpaQueryFactory
                .select(book.count())
                .from(book)
                .where(
                        categoryNameEq(categoryName),
                        titleContains(bookTitle)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
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
}
