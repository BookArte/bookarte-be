package com.library.bookarte.recommendation.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.recommendation.dto.RecommendationReqDto;
import com.library.bookarte.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/set")
    public ResponseEntity<GlobalResponseDto<String>> setRecommendByAdmin(@RequestBody RecommendationReqDto recommendationReqDto) {
        recommendationService.setRecommendBookByAdmin(recommendationReqDto);

        String result = "도서 추천 순위 등록 완료";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED, result));
    }
}
