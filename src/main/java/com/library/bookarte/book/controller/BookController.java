package com.library.bookarte.book.controller;

import com.library.bookarte.book.dto.BookReqDto;
import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.service.BookService;
import com.library.bookarte.global.response.GlobalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    //도서 등록
    @PostMapping("/register")
    public ResponseEntity<GlobalResponseDto> registerBook(@RequestBody BookReqDto bookReqDto){
        BookResDto result = bookService.registerBook(bookReqDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED,result));
    }

    //도서 상제 조회
    @GetMapping("/view/{bookId}")
    public ResponseEntity<GlobalResponseDto> findBookById(@PathVariable("bookId") Long bookId){
        BookResDto result = bookService.findBookById(bookId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    //도서 정보 수정
    @PatchMapping("/{bookId}")
    public ResponseEntity<GlobalResponseDto> updateBook(@PathVariable("bookId") Long bookId,
                                                        @RequestBody BookReqDto bookReqDto) {
        Long result = bookService.updateBook(bookId, bookReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    //도서 삭제
    @DeleteMapping("/{bookId}")
    public ResponseEntity<GlobalResponseDto> deleteBook(@PathVariable("bookId") Long bookId){

        bookService.deleteBook(bookId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,null));

    }

    //도서 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<GlobalResponseDto> listBook(@PageableDefault(page = 1) Pageable pageable){
        Page<Book> result = bookService.findAllBooks(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }

}
