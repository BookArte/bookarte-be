package com.library.bookarte.book.service;

import com.library.bookarte.book.dto.BookDto;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = CustomException.class)
public class BookService {

    private final BookRepository bookRepository;

    public BookDto registerBook(BookDto bookDto){

        Book book = Book.builder()
                .bookTitle(bookDto.getBookTitle())
                .bookAuthor(bookDto.getBookAuthor())
                .publisherName(bookDto.getPublisherName())
                .publicationDate(bookDto.getPublicationDate())
                .bookIsbn(bookDto.getBookIsbn())
                .bookContents(bookDto.getBookContents())
                .bookBorrowYn('Y')
                .bookCallNumber(bookDto.getBookCallNumber())
                .bookThumbnail(bookDto.getBookThumbnail())
                .build();

        bookRepository.save(book);

        return bookDto;
    }

    @Transactional
    public BookDto findBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        BookDto bookDto = BookDto.builder()
                .bookTitle(book.getBookTitle())
                .bookAuthor(book.getBookAuthor())
                .bookContents(book.getBookContents())
                .bookCallNumber(book.getBookCallNumber())
                .bookIsbn(book.getBookIsbn())
                .bookThumbnail(book.getBookThumbnail())
                .build();

        return bookDto;
    }
}
