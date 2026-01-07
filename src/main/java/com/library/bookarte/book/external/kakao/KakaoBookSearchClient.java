package com.library.bookarte.book.external.kakao;

import com.library.bookarte.book.external.dto.BookSearchResult;
import com.library.bookarte.book.external.kakao.dto.KakaoBookDocument;
import com.library.bookarte.book.external.kakao.dto.KakaoBookResponse;
import lombok.RequiredArgsConstructor;
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
                .bookTitle(doc.getTitle())
                .bookAuthor(String.join(", ", doc.getAuthors()))
                .publisherName(doc.getPublisher())
                .bookTranslator(String.join(", ", doc.getTranslators()))
                .publicationDate(doc.getDatetime())
                .bookIsbn(extractIsbn13(doc.getIsbn()))
                .bookThumbnail(doc.getThumbnail())
                .bookContents(doc.getContents())
                .build();
    }

    //ISBN 추출
    private String extractIsbn13(String isbn) {
        if (isbn == null) return null;
        String[] parts = isbn.split(" ");
        return parts.length > 1 ? parts[1] : parts[0];
    }
}