package com.library.bookarte.book.controller;

import com.library.bookarte.book.dto.BookDto;
import com.library.bookarte.book.service.BookService;
import com.library.bookarte.global.response.GlobalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    @PostMapping("/register")
    public ResponseEntity<GlobalResponseDto> registerBook(@RequestBody BookDto bookDto){
        BookDto result = bookService.registerBook(bookDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED,result));
    }

    @GetMapping("/view/{bookId}")
    public ResponseEntity<GlobalResponseDto> findBookById(@PathVariable("bookId") Long bookId){
        BookDto result = bookService.findBookById(bookId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @PatchMapping("/{bookId}/update")
    public ResponseEntity<GlobalResponseDto> updateBook(@PathVariable("bookId") Long bookId,
                                                        @RequestBody BookDto bookDto) {
        Long result = bookService.updateBook(bookId, bookDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }








}
