package com.library.bookarte.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchFilterDto {

    private final String bookTitle;
    private final String category;
}
