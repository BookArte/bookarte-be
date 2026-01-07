package com.library.bookarte.book.repository;

import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
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

import java.util.List;

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


        List<BookResDto> content = jpaQueryFactory
                .select(Projections.constructor(
                        BookResDto.class,
                        book.bookId,
                        book.bookTitle,
                        book.publisherName,
                        book.publicationDate,
                        book.bookIsbn,
                        book.bookContents,
                        book.bookThumbnail,
                        book.bookCallNumber,
                        category.categoryName
                ))
                .from(book)
                .join(book.category, category)
                .where(
                        categoryNameEq(categoryName),
                        titleContains(keyword)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

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
