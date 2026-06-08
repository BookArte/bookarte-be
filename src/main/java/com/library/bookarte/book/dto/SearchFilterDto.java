package com.library.bookarte.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilterDto {
    private String bookTitle;
    private String category;
    private String bookIsbn;
    private String publisherName;
    private String bookAuthor;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDateStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDateEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAtStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAtEnd;
}
