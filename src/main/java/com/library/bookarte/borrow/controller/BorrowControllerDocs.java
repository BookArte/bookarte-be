package com.library.bookarte.borrow.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
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
}
