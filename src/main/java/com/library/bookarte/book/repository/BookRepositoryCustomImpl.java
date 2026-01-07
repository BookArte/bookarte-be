package com.library.bookarte.book.repository;

import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.entity.Book;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.group.GroupBy.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
        String keyword = searchFilterDto.getKeyword();

        //도서 id만 선 조회
        List<Long> ids = jpaQueryFactory
                .select(book.bookId)
                .from(book)
                .join(book.category, category)
                .where(
                        categoryNameEq(categoryName),
                        titleContains(keyword)
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
                        titleContains(keyword)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // ===== 조건 메서드 =====

    private BooleanExpression categoryNameEq(String categoryName) {
        return categoryName != null
                ? book.category.categoryName.eq(categoryName)
                : null;
    }

    private BooleanExpression titleContains(String keyword) {
        return StringUtils.hasText(keyword)
                ? book.bookTitle.contains(keyword)
                : null;
    }

}
