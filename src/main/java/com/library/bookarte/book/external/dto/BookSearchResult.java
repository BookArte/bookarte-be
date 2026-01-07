package com.library.bookarte.book.external.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookSearchResult {

    //책제목
    private String bookTitle;

    //책저자
    private String bookAuthor;

    //번역가
    private String bookTranslator;

    //출판사
    private String publisherName;

    //춮판일
    private String publicationDate;

    //ISBN
    private String bookIsbn;

    //책소개
    private String bookContents;

    //썸네일
    private String bookThumbnail;

    //카테고리
    private String bookCategory;
}