package com.library.bookarte.borrow.controller;

import com.library.bookarte.borrow.service.BorrowService;
import com.library.bookarte.global.response.GlobalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/borrow")
public class BorrowController implements BorrowControllerDocs {

    private final BorrowService borrowService;

    @Override
    public ResponseEntity<GlobalResponseDto<String>> borrowBook(@RequestParam long bookId){
        borrowService.borrowBook(bookId);
        String result = "대출 완료";
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }
}
