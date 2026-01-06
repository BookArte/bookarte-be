package com.library.bookarte.book.external.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BookSearchResult {
    private String title;
    private String author;
    private String publisher;
    private String translator;
    private String publishedDate;
    private String isbn;
    private String thumbnail;
    private String description;
    private String callNumber;
    private String category;
}