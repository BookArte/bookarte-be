package com.library.bookarte.book.repository;

import com.library.bookarte.book.entity.Book;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
    boolean existsByBookIsbn(String isbn);

    //비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.bookId = :id")
    Optional<Book> findByIdWithPessimisticLock(@Param("id") Long id);

    //낙관적 락
/*    @Query("select b from Book b where b.bookId = :id")
    Optional<Book> findByIdWithOptimisticLock(@Param("id") Long id);*/

    @Query("SELECT MAX(b.createdAt) FROM Book b")
    Optional<LocalDateTime> findLatestCreatedAt();
}
