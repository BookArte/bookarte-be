package com.library.bookarte.recommendation.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.recommendation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Recommendation")
public interface RecommendationControllerDocs {

    @Operation(summary = "추천 도서 설정 요청", description = "**성공 응답 데이터:**: 추천 설정 완료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "추천 설정 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PostMapping("/set")
    ResponseEntity<GlobalResponseDto<String>> setRecommendByAdmin(@RequestBody RecommendationReqDto recommendationReqDto);

    @Operation(summary = "추천 도서 제외 요청", description = "**성공 응답 데이터:**: 추천 제외 완료")
    @Parameter(name = "recommendationId", description = "제외할  추천 id", example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 제외 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 추천 도서 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @DeleteMapping("/{recommendationId}")
    ResponseEntity<GlobalResponseDto<String>> deleteRecommend(@PathVariable Long recommendationId);

    @Operation(summary = "추천 도서 목록 조회 요청", description = "**성공 응답 데이터:**: 추천 도서 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 도서 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/pick-10/list")
    ResponseEntity<GlobalResponseDto<List<RecommendationBookResDto>>> pickList();

    @Operation(summary = "추천 도서 정보 단일 조회", description = "**성공 응답 데이터:** 단일 추천 도서 정보")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 도서 정보 단일 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한없음"),
            @ApiResponse(responseCode = "404", description = "추천 도서 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/{recommendationId}")
    ResponseEntity<GlobalResponseDto<RecommendationBookResDto>> getRecommendationDetail(@PathVariable Long recommendationId);


    @Operation(summary = "추천 도서 순위 변경 요청", description = "**성공 응답 데이터:**: 추천 도서 순위 변경 완료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 도서 순위 변경 완료"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 도서 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/reorder")
    ResponseEntity<GlobalResponseDto<String>> reorder(@RequestBody ReorderReqDto reorderReqDto);

    @Operation(summary = "추천 도서 정보 수정 요청", description = "**성공 응답 데이터:**: 추천 도서 정보 수정 완료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 도서 정보 수정 완료"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 추천 도서 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/{recommendationId}")
    ResponseEntity<GlobalResponseDto<String>> updateRecommend(@PathVariable Long recommendationId,
                                                             @RequestBody UpdateRecommendDto updateRecommendDto);


    @Operation(summary = "진행 및 예약된 추천 도서 목록 조회 요청", description = "**성공 응답 데이터:** 진행 및 예약된 추천 도서 목록 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "진행 및 예약된 추천 도서 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/admin/active-list")
    ResponseEntity<GlobalResponseDto<List<RecommendationBookResDto>>> getActiveRecommendationList();

    @Operation(summary = "만료된 추천 도서 이력 목록 조회 요청", description = "**성공 응답 데이터:** 만료된 추천 도서 이력 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "만료된 추천 도서 이력 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/admin/history")
    ResponseEntity<GlobalResponseDto<Page<RecommendationBookResDto>>> getRecommendationHistory(@ParameterObject @PageableDefault(sort = "endDate", direction = Sort.Direction.DESC)Pageable pageable,
                                                                                               @ModelAttribute RecSearchFilterDto recSearchFilterDto);
}
