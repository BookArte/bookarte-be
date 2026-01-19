package com.library.bookarte.category.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryReqDto {
    private String categoryCode;
    private String categoryName;
}
