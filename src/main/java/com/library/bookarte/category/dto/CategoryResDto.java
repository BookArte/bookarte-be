package com.library.bookarte.category.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResDto {
    private Long categoryId;
    private String categoryName;
}
