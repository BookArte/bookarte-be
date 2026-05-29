package com.library.bookarte.book.external.national;

import com.library.bookarte.book.external.national.dto.NationalLibraryCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class NationalLibrarySearchClient {

    private final RestTemplate restTemplate;

    @Value("${national.api.key}")
    private String nationalApiKey;

    @Value("${national.api.url}")
    private String nationalApiUrl;

    public String fetchCategoryByTitle(String title) {

        String url = nationalApiUrl +
                        "?key=" + nationalApiKey +
                        "&kwd=" + title
                    + "&page_no=1"
                    + "&page_size=1";

        NationalLibraryCategoryResponse response =
                restTemplate.getForObject(url, NationalLibraryCategoryResponse.class);


        return response != null ? response.getCategory() : null;

    }

}