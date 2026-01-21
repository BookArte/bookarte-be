package com.library.bookarte.book.controller;

import com.library.bookarte.book.dto.BookReqDto;
import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.external.dto.BookSearchResult;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book")
public interface BookControllerDocs {

    /*Create: 도서 등록*/
    @Operation(summary = "도서 등록 요청", description = "**성공 응답 데이터:** 도서 정보 저장 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "도서 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "카테고리 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PostMapping("/register")
    ResponseEntity<GlobalResponseDto<String>> registerBook(@RequestBody BookReqDto bookReqDto);

    /*Read: 단일 도서 정보 조회*/
    @Operation(summary = "단일 도서 조회 요청", description = "**성공 응답 데이터:** 단일 도서 정보")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단일 도서 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 도서가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/view/{bookId}")
    ResponseEntity<GlobalResponseDto<BookResDto>> findBookById(@PathVariable("bookId") Long bookId);

    /*Read: 도서 목록 조회*/
    @Operation(summary = "도서 목록 조회 요청", description = "**성공 응답 데이터:** 도서 목록 정보")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/list")
    ResponseEntity<GlobalResponseDto<Page<BookResDto>>> listBook(@ParameterObject  @ModelAttribute SearchFilterDto searchFilterDto,
                                                                 @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

    /*Update: 도서 정보 수정*/
    @Operation(summary = "도서 정보 수정 요청", description = "**성공 응답 데이터:** 도서의 `bookId`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 도서가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/{bookId}")
    ResponseEntity<GlobalResponseDto<Long>> updateBook(@PathVariable("bookId") Long bookId,
                                                       @RequestBody BookReqDto bookReqDto);

    @Operation(summary = "도서 삭제 요청", description = "**성공 응답 데이터:** 도서 삭제 성공`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 도서가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @DeleteMapping("/{bookId}")
    ResponseEntity<GlobalResponseDto<?>> deleteBook(@PathVariable("bookId") Long bookId);

    @Operation(summary = "외부 api 도서 정보 검색", description = "**성공 응답 데이터:** 해당되는 도서 목록`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "외부 api 요청 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/library/search")
    ResponseEntity<GlobalResponseDto<List<BookSearchResult>>> searchBookWithLibraryApi(@RequestParam String query);
}
