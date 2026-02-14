package com.library.bookarte.borrow.controller;

import com.library.bookarte.borrow.dto.BorrowSearchFilterDto;
import com.library.bookarte.borrow.dto.response.MonthlyData;
import com.library.bookarte.borrow.dto.response.TotalBorrowResDto;
import com.library.bookarte.borrow.dto.response.UserBorrowResDto;
import com.library.bookarte.borrow.service.BorrowService;
import com.library.bookarte.global.response.GlobalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/borrow")
public class BorrowController implements BorrowControllerDocs {

    private final BorrowService borrowService;

    @Override
    public ResponseEntity<GlobalResponseDto<String>> borrowBook(@PathVariable Long bookId){
        borrowService.borrowBook(bookId);
        String result = "대출 완료";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED,result));
    }

    @Override
    public  ResponseEntity<GlobalResponseDto<Page<TotalBorrowResDto>>> getTotalBorrows(@ModelAttribute BorrowSearchFilterDto borrowSearchFilterDto,
                                                                                       Pageable pageable) {
        Page<TotalBorrowResDto> result = borrowService.getTotalBorrows(borrowSearchFilterDto, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Page<UserBorrowResDto>>> getUserBorrows(@ModelAttribute BorrowSearchFilterDto borrowSearchFilterDto,
                                                                                    Pageable pageable) {
        Page<UserBorrowResDto> result = borrowService.getUserBorrows(borrowSearchFilterDto, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<String>> requestReturn(@PathVariable Long borrowId){
        borrowService.requestReturnBook(borrowId);
        String result = "도서 반납 신청 완료";
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<String>> approveReturn(@PathVariable Long borrowId){
        borrowService.approveReturnBook(borrowId);
        String result = "도서 반납 승인 완료";
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<String>> extendReturn(@PathVariable Long borrowId) {
        borrowService.extendReturnDate(borrowId);
        String result = "도서 대출 기간 연장 완료";
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<List<MonthlyData>>> rollingYear(@PathVariable Long bookId){
        List<MonthlyData> result = borrowService.getRollingYearHistory(bookId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }


}
