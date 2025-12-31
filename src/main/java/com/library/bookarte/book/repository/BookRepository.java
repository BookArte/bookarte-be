package com.library.bookarte.book.repository;

import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.entity.Book;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(
            value = """
        select new com.library.bookarte.book.dto.BookResDto(
            b.bookId,
            b.bookTitle,
            b.bookAuthor,
            b.publisherName,
            b.publicationDate,
            b.bookIsbn,
            b.bookContents,
            b.bookThumbnail,
            b.bookCallNumber,
            c.bookCategoryName
        )
        from Book b
        join b.category c
        where c.categoryId = :categoryId
    """,
            countQuery = """
        select count(b)
        from Book b
        where b.category.categoryId = :categoryId
    """
    )
    Page<BookResDto> findBookResDtosByCategoryId(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
