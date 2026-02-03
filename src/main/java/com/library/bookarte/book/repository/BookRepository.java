package com.library.bookarte.book.repository;

import com.library.bookarte.book.entity.Book;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
/*    @Query(
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
            c.categoryName
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
    );*/

    boolean existsByBookIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.bookId = :id")
    Optional<Book> findByIdWithLock(@Param("id") Long id);
}
