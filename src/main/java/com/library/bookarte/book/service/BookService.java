package com.library.bookarte.book.service;

import com.library.bookarte.book.dto.BookDto;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class BookService {

    private final BookRepository bookRepository;

    /*도서 등록 api*/
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

    /*도서 상세 조회 api*/
    @Transactional(readOnly = true)
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

    public Long updateBook(Long bookId,BookDto bookDto){

        Book updateTargetBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        updateTargetBook.updateBook(bookDto.getBookTitle(),
                bookDto.getBookAuthor(),
                bookDto.getPublisherName(),
                bookDto.getPublicationDate(),
                bookDto.getBookIsbn(),
                bookDto.getBookContents(),
                bookDto.getBookCallNumber(),
                bookDto.getBookThumbnail());

        return bookId;
    }

    public void deleteBook(Long bookId) {
        Book deleteTargetBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(deleteTargetBook);
    }
}
