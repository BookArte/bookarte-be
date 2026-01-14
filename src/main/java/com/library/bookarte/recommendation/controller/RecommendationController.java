package com.library.bookarte.recommendation.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.recommendation.dto.RecommendationBookResDto;
import com.library.bookarte.recommendation.dto.RecommendationReqDto;
import com.library.bookarte.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<GlobalResponseDto<String>> deleteRecommend(@PathVariable Long recommendationId){
        recommendationService.deleteRecommendBook(recommendationId);

        String result = "도서 추천 순위 삭제 완료";
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @GetMapping("/pick-10/list")
    public ResponseEntity<GlobalResponseDto<List<RecommendationBookResDto>>> pickList(){
        List<RecommendationBookResDto> result = recommendationService.getRecommendationBooks();

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

}
