package com.library.bookarte.book.controller;

import com.library.bookarte.book.dto.BookDto;
import com.library.bookarte.book.service.BookService;
import com.library.bookarte.global.response.GlobalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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




}
