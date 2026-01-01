package com.library.bookarte.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchFilterDto {

    private final String keyword;
    private final String category;
}
