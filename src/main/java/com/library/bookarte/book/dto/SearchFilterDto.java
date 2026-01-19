package com.library.bookarte.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SearchFilterDto {

    private final String bookTitle;
    private final String category;
    private final String isbn;
    private final String publisherName;
    private final String author;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate publicationDateStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate publicationDateEnd;

}
