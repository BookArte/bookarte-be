package com.library.bookarte.book.repository;

import com.library.bookarte.book.dto.response.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookRepositoryCustom {
    Page<BookResDto> findBooks(SearchFilterDto searchFilterDto, Pageable pageable);
    Optional<BookResDto> findBookDetailWithWish(Long bookId, Long memberId);
    List<Book> findBooksAlsoBorrowed(Long bookId, Set<Long> excludeIds);
    List<Book> findBooksByAuthorOrderByBorrowCount(String authorName, Set<Long> excludeIds, int limit);
    List<Book> findBooksByCategoryOrderByBorrowCount(String category, Set<Long> excludeIds, int limit);
    List<String> skippedTitles(List<Long> bookIds);
    List<Long> deletableBookIds(List<Long> bookIds);
    long deleteBooksByIds(List<Long> bookIds);
}
