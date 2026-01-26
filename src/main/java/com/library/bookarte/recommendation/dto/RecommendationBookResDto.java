package com.library.bookarte.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationBookResDto {
    //추천 정보
    private Long recommendationId;

    private int priority;

    private String comments;

    private String recommendType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;


    //도서 정보
    private Long bookId;

    private String bookTitle;

    private String bookAuthor;

    private String publisherName;

    private String bookTranslator;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    private String bookIsbn;

    private String bookContents;

    private String bookThumbnail;

    private String bookCallNumber;

    private String bookCategoryName;

}
