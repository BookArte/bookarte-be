package com.library.bookarte.borrow.controller;

import com.library.bookarte.borrow.dto.TotalBorrowResDto;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Borrow")
public interface BorrowControllerDocs {

    /*Create: 도서 대출*/
    @Operation(summary = "도서 대출 요청", description = "**성공 응답 데이터:** 도서 대출 요청 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "도서 대출 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "403", description = "이미 대출 중인 도서"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 도서"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PostMapping
    ResponseEntity<GlobalResponseDto<String>> borrowBook(@RequestParam long bookId);

    /*Read: 관리자용 전체 대출 정보 목록 조회*/
    @Operation(summary = "관리자 전체 대출 정보 목록 조회", description = "**성공 응답 데이터:** 전체 대출 정보 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "전체 대출 몱록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/admin/list")
    ResponseEntity<GlobalResponseDto<Page<TotalBorrowResDto>>> getTotalBorrows(@ParameterObject Pageable pageable);

}
