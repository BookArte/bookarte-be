package com.library.bookarte.wish.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.wish.dto.response.WishResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Wish")
public interface WishControllerDocs {
    /*Create: 관심 도서 추가*/
    @Operation(summary = "관심 도서 추가 요청", description = "**성공 응답 데이터:** 관심 도서 추가 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "관심 도서 추가 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "404", description = "도서 조회 불가"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PostMapping("/{bookId}")
    ResponseEntity<GlobalResponseDto<String>> addWishBook(@PathVariable Long bookId,
                                                          @AuthenticationPrincipal Long memberId);

    /*Read: 관심 도서 목록 조회*/
    @Operation(summary = "관심 도서 목록 조회 요청", description = "**성공 응답 데이터:** 관심 도서 목록 조회 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관심 도서 추가 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping
    ResponseEntity<GlobalResponseDto<Page<WishResDto>>> getWishList(@AuthenticationPrincipal Long memberId,
                                                                    @ParameterObject Pageable pageable);

    /*Read: 관심 도서 목록 조회*/
    @Operation(summary = "관심 도서 삭제 요청", description = "**성공 응답 데이터:** 관심 도서 삭제 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관심 도서 추가 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "사용자 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @DeleteMapping("/{wishId}")
    ResponseEntity<GlobalResponseDto<String>> deleteWish(@AuthenticationPrincipal Long memberId,@PathVariable Long wishId);


}
