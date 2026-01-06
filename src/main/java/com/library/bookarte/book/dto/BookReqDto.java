package com.library.bookarte.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReqDto {

    //책제목
    private String bookTitle;

    //책저자
    private String bookAuthor;

    //번역가
    private String bookTranslator;

    //출판사
    private String publisherName;

    //출판일
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    //ISBN
    private String bookIsbn;

    //책소개
    private String bookContents;

    //썸네일
    private String bookThumbnail;

    //청구기호
    private String bookCallNumber;

    //카테고리
    private String bookCategory;

}
