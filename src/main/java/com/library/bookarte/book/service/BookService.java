package com.library.bookarte.book.service;

import com.library.bookarte.book.dto.BookReqDto;
import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class BookService {

    private final BookRepository bookRepository;
    private final int defaultSize = 5;


    /*도서 등록 api*/
    public BookResDto registerBook(BookReqDto bookReqDto){

        Book book = Book.builder()
                .bookTitle(bookReqDto.getBookTitle())
                .bookAuthor(bookReqDto.getBookAuthor())
                .publisherName(bookReqDto.getPublisherName())
                .publicationDate(bookReqDto.getPublicationDate())
                .bookIsbn(bookReqDto.getBookIsbn())
                .bookContents(bookReqDto.getBookContents())
                .bookBorrowYn('Y')
                .bookCallNumber(bookReqDto.getBookCallNumber())
                .bookThumbnail(bookReqDto.getBookThumbnail())
                .build();

        bookRepository.save(book);

        return BookResDto.builder()
                .bookId(book.getBookId())
                .bookTitle(book.getBookTitle())
                .bookAuthor(book.getBookAuthor())
                .bookContents(book.getBookContents())
                .bookCallNumber(book.getBookCallNumber())
                .bookIsbn(book.getBookIsbn())
                .bookThumbnail(book.getBookThumbnail())
                .build();
    }

    /*도서 상세 조회 api*/
    @Transactional(readOnly = true)
    public BookResDto findBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        return BookResDto.builder()
                .bookId(book.getBookId())
                .bookTitle(book.getBookTitle())
                .bookAuthor(book.getBookAuthor())
                .bookContents(book.getBookContents())
                .bookCallNumber(book.getBookCallNumber())
                .bookIsbn(book.getBookIsbn())
                .bookThumbnail(book.getBookThumbnail())
                .build();
    }

    /*도서 수정 조회 api*/
    public Long updateBook(Long bookId,BookReqDto bookReqDto){

        Book updateTargetBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        updateTargetBook.updateBook(bookReqDto.getBookTitle(),
                bookReqDto.getBookAuthor(),
                bookReqDto.getPublisherName(),
                bookReqDto.getPublicationDate(),
                bookReqDto.getBookIsbn(),
                bookReqDto.getBookContents(),
                bookReqDto.getBookCallNumber(),
                bookReqDto.getBookThumbnail());

        return bookId;
    }

    /*도서 삭제 조회 api*/
    public void deleteBook(Long bookId) {
        Book deleteTargetBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(deleteTargetBook);
    }

    /*도서 전체 조회 api*/
    public Page<BookResDto> findAllBooks(Pageable pageable){
        int page = pageable.getPageNumber() - 1;

        Page<Book> books = bookRepository.findAll(PageRequest.of(page, defaultSize, Sort.Direction.DESC, "bookId"));

        List<BookResDto> bookResDtoList = books.getContent().stream()
                .map(Book::toBookResDto)
                .toList();

        return new PageImpl<>(bookResDtoList, pageable, books.getTotalElements());

    }

}
