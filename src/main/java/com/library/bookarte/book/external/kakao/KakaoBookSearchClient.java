package com.library.bookarte.book.external.kakao;

import com.library.bookarte.book.external.dto.BookSearchResult;
import com.library.bookarte.book.external.kakao.dto.KakaoBookDocument;
import com.library.bookarte.book.external.kakao.dto.KakaoBookResponse;
import lombok.RequiredArgsConstructor;
import nonapi.io.github.classgraph.json.JSONUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoBookSearchClient{

    private final WebClient kakaoWebClient;

    @Value("${kakao.api.request-path}")
    private String requestPath;


    public List<BookSearchResult> search(String keyword) {
        KakaoBookResponse response = kakaoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(requestPath)
                        .queryParam("query", keyword)
                        .build())
                .retrieve()
                .bodyToMono(KakaoBookResponse.class)
                .block();


        return response.getDocuments().stream()
                .map(this::toResult)
                .toList();
    }

    private BookSearchResult toResult(KakaoBookDocument doc) {
        return BookSearchResult.builder()
                .title(doc.getTitle())
                .author(String.join(", ", doc.getAuthors()))
                .publisher(doc.getPublisher())
                .translator(String.join(", ", doc.getTranslators()))
                .publishedDate(doc.getDatetime())
                .isbn(extractIsbn13(doc.getIsbn()))
                .thumbnail(doc.getThumbnail())
                .description(doc.getContents())
                .build();
    }

    //ISBN 추출
    private String extractIsbn13(String isbn) {
        if (isbn == null) return null;
        String[] parts = isbn.split(" ");
        return parts.length > 1 ? parts[1] : parts[0];
    }
}