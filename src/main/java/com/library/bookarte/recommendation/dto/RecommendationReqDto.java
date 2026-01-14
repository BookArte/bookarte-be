package com.library.bookarte.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationReqDto {

    //도서
    private Long bookId;

    //추천 타입
    private String recommendType;

    //추천 코멘트
    private String comments;

    //추천 시작일
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    //추천 종료일
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
