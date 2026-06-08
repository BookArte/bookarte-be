package com.library.bookarte.borrow.controller;

import com.library.bookarte.borrow.dto.BorrowSearchFilterDto;
import com.library.bookarte.borrow.dto.response.MonthlyData;
import com.library.bookarte.borrow.dto.response.PopularBookResDto;
import com.library.bookarte.borrow.dto.response.TotalBorrowResDto;
import com.library.bookarte.borrow.dto.response.UserBorrowResDto;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/{bookId}")
    ResponseEntity<GlobalResponseDto<String>> borrowBook(@PathVariable Long bookId,@AuthenticationPrincipal Long memberId);

    /*Read: 사용자용 전체 대출 정보 목록 조회*/
    @Operation(summary = "사용자 전체 정보 목록 조회", description = "**성공 응답 데이터:** 사용자 대출 정보 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 대출 몱록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping
    ResponseEntity<GlobalResponseDto<Page<UserBorrowResDto>>> getUserBorrows(@ParameterObject @ModelAttribute BorrowSearchFilterDto borrowSearchFilterDto,
                                                                             @AuthenticationPrincipal Long memberId,
                                                                             @ParameterObject Pageable pageable);
    /*Update: 대출 도서 반납 신청*/
    @Operation(summary = "도서 반납 신청 요청", description = "**성공 응답 데이터:** 도서 반납 신청 요청 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 반납 신청 요청 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 도서 대출 이력"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/request-return/{borrowId}")
    ResponseEntity<GlobalResponseDto<String>> requestReturn(@PathVariable Long borrowId);

    @Operation(summary = "도서 대출 기간 연장 요청", description = "**성공 응답 데이터:** 도서 대출 기간 연장 요청 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 반납 신청 요청 성공"),
            @ApiResponse(responseCode = "400", description = "기간 연장할 수 없는 도서"),
            @ApiResponse(responseCode = "400", description = "대출 중인 아닌 도서"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 도서 대출 이력"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/extend/{borrowId}")
    ResponseEntity<GlobalResponseDto<String>> extendReturn(@PathVariable Long borrowId, @AuthenticationPrincipal Long memberId);

    /*Read: 조회 시점 1년 단위 대출 횟수 조회*/
    @Operation(summary = "도서별 1년 간 대출 횟수", description = "**성공 응답 데이터:** 도서별 1년 간 대출 횟수 데이터")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 도서 반납 승인 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/{bookId}")
    ResponseEntity<GlobalResponseDto<List<MonthlyData>>> rollingYear(@PathVariable Long bookId);

    /*Read: 관리자용 전체 대출 정보 목록 조회*/
    @Operation(summary = "관리자 전체 대출 정보 목록 조회", description = "**성공 응답 데이터:** 전체 대출 정보 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 대출 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/admin")
    ResponseEntity<GlobalResponseDto<Page<TotalBorrowResDto>>> getTotalBorrows(@ParameterObject @ModelAttribute BorrowSearchFilterDto borrowSearchFilterDto,
                                                                               @ParameterObject Pageable pageable);

    /*Update: 관리자 도서 반납 승인*/
    @Operation(summary = "관리자 도서 반납 승인", description = "**성공 응답 데이터:** 관리자 도서 반납 승인 완료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 도서 반납 승인 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/admin/{borrowId}")
    ResponseEntity<GlobalResponseDto<String>> approveReturn(@PathVariable Long borrowId);

    /*Read: 인기 대출 도서 목록 조회*/
    @Operation(summary = "인기 대출 도서 목록 조회", description = "**성공 응답 데이터:** 인기 대출 도서 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 반납 신청 요청 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/popular")
    ResponseEntity<GlobalResponseDto<Page<PopularBookResDto>>> getPopularBooks(@RequestParam("period") String period,
                                                                               @ParameterObject Pageable pageable);

    /*Read: 특정 사용자의 대출 목록 확인*/
    @Operation(summary = "특정 사용자의 대출 목록 확인", description = "**성공 응답 데이터:** 대출 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "패널티 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/admin/user_list/{memberId}")
    ResponseEntity<GlobalResponseDto<List<UserBorrowResDto>>> getMemberBorrow(@PathVariable Long memberId);
}
