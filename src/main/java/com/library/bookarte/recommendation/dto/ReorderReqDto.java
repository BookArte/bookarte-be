package com.library.bookarte.recommendation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReorderReqDto {
    private List<Long> reorderedIds;
}
