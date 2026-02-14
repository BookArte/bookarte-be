package com.library.bookarte.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationReqDto {

    //도서
    @NotNull(message = "추천 도서는 필수 입력 항목입니다.")
    private Long bookId;

    //추천 타입
    private String recommendType;

    //추천 코멘트
    @NotBlank(message = "추천 코멘트는 필수 입력 항목입니다.")
    private String comments;

    //추천 시작일
    @NotNull(message = "추천 시작일은 필수 입력 항목입니다.")
    @FutureOrPresent(message = "추천 시작일은 과거 날짜일 수 없습니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    //추천 종료일
    @NotNull(message = "추천 종료일은 필수 입력 항목입니다.")
    @FutureOrPresent(message = "추천 시작일은 과거 날짜일 수 없습니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
