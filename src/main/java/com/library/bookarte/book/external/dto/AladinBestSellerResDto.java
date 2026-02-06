package com.library.bookarte.book.external.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AladinBestSellerResDto {
    private String bookTitle;
    private String bookAuthor;
    private String bookTranslator;
    private String publisherName;
    private String publicationDate;
    private String bookIsbn;
    private String bookContents;
    private String bookThumbnail;

    private int bestRank;
    private String bestDuration;
    private int customerReviewRank;
}
