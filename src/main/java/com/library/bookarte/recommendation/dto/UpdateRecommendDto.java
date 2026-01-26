package com.library.bookarte.recommendation.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRecommendDto {

    private String comments;
    private LocalDate startDate;
    private LocalDate endDate;

}
