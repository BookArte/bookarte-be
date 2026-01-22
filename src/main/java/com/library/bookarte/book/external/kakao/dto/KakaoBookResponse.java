package com.library.bookarte.book.external.kakao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KakaoBookResponse {

    private List<KakaoBookDocument> documents;
}