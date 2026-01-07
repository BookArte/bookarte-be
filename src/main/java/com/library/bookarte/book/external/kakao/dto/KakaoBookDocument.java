package com.library.bookarte.book.external.kakao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KakaoBookDocument {

    private String title;
    private List<String> authors;
    private List<String> translators;
    private String publisher;
    private String datetime;
    private String isbn;
    private String thumbnail;
    private String contents;
}
