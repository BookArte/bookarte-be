package com.library.bookarte.recommendation.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.recommendation.dto.RecommendationBookResDto;
import com.library.bookarte.recommendation.dto.RecommendationReqDto;
import com.library.bookarte.recommendation.dto.ReorderReqDto;
import com.library.bookarte.recommendation.dto.UpdateRecommendDto;
import com.library.bookarte.recommendation.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController implements RecommendationControllerDocs {
    private final RecommendationService recommendationService;

    @Override
    public ResponseEntity<GlobalResponseDto<String>> setRecommendByAdmin(@Valid @RequestBody RecommendationReqDto recommendationReqDto) {
        recommendationService.setRecommendBookByAdmin(recommendationReqDto);

        String result = "추천 등록 완료";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<String>> deleteRecommend(@PathVariable Long recommendationId){
        recommendationService.deleteRecommendBook(recommendationId);

        String result = "추천 제외 완료";
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<List<RecommendationBookResDto>>> pickList(){
        List<RecommendationBookResDto> result = recommendationService.getRecommendationBooks();

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<String>> reorder(@RequestBody ReorderReqDto reorderReqDto){
        recommendationService.reorderRecommendation(reorderReqDto);

        String result = "추천 순위 변경 완료";
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));

    }

    @Override
    public ResponseEntity<GlobalResponseDto<String>> updateRecommend(@PathVariable Long recommendationId,
                                                             @RequestBody UpdateRecommendDto updateRecommendDto){
        recommendationService.updateRecommend(recommendationId, updateRecommendDto);

        String result = "추천 도서 정보 수정 완료";

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Boolean>> isRecommend(@RequestParam Long bookId) {
        boolean result = recommendationService.existByBookId(bookId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }
}
