package com.library.bookarte.category.controller;

import com.library.bookarte.category.dto.CategoryReqDto;
import com.library.bookarte.category.dto.CategoryResDto;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category")
public interface CategoryControllerDocs {

    /*Create: 카테고리 등록*/
    @Operation(summary = "카테고리 등록 요청", description = "**성공 응답 데이터:** 카테고리의 `categoryId`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PostMapping
    ResponseEntity<GlobalResponseDto<Long>> generateCategory(@RequestBody CategoryReqDto categoryReqDto);

    /*Read: 단일 카테고리 정보 조회*/
    @Operation(summary = "단일 카테고리 정보 조회 요청", description = "**성공 응답 데이터:** `카테고리 응답 객체`")
    @Parameter(name = "categoryId", description = "단일 조회할 카테고리 id", example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단일 카테고리 성공"),
            @ApiResponse(responseCode = "404", description = "해당 카테고리 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/{categoryId}")
    ResponseEntity<GlobalResponseDto<CategoryResDto>> findByCategoryId(@PathVariable Long categoryId);

    /*Read: 카테고리 목록 정보 조회*/
    @Operation(summary = "카테고리 목록 정보 조회 요청", description = "**성공 응답 데이터:** `카테고리 응답 객체 목록`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 목록 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/list")
    ResponseEntity<GlobalResponseDto<List<CategoryResDto>>> findAllCategory();

    /*Update: 카테고리 정보 수정*/
    @Operation(summary = "카테고리 정보 수정 요청", description = "**성공 응답 데이터:** 카테고리의 `categoryId`")
    @Parameter(name = "categoryId", description = "정보 수정할 카테고리 id", example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 카테고리 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/{categoryId}")
    ResponseEntity<GlobalResponseDto<Long>> updateCategory(@PathVariable Long categoryId,
                                                           @RequestBody CategoryReqDto categoryReqDto);

    /*Delete: 카테고리 정보 삭제*/
    @Operation(summary = "카테고리 정보 삭제 요청", description = "**성공 응답 데이터:** 카테고리 정보 삭제 성공")
    @Parameter(name = "categoryId", description = "삭제할 카테고리 id", example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 정보 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 카테고리 존재하지 않음"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    ResponseEntity<GlobalResponseDto<?>> deleteCategory(@PathVariable Long categoryId);
}
