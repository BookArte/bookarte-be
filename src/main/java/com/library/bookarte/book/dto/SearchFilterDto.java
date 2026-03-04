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
    private final String bookIsbn;
    private final String publisherName;
    private final String bookAuthor;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate publicationDateStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate publicationDateEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate createdAtStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate createdAtEnd;
}
