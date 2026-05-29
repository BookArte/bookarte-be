package com.library.bookarte.book.external.aladin;

import com.library.bookarte.book.dto.response.BestsellerResponse;
import com.library.bookarte.book.external.aladin.dto.AladinResponse;
import com.library.bookarte.book.external.dto.AladinBestSellerResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AladinClient {

    @Value("${aladin.api.url}")
    private String aladinApiUrl;

    @Value("${aladin.api.key}")
    private String aladinApikey;
    public BestsellerResponse getBestSellers(String type, int page, int size) {
        RestTemplate restTemplate = new RestTemplate();

        String url = aladinApiUrl +
                "?ttbkey=" + aladinApikey +
                "&QueryType=" + type +
                "&MaxResults=" + size +
                "&start=" + page +
                "&SearchTarget=Book" +
                "&output=xml"+ "&Version=20131101";

        AladinResponse aladinResponse = restTemplate.getForObject(url, AladinResponse.class);

        if (aladinResponse == null || aladinResponse.getItems() == null) {
            return BestsellerResponse.builder().books(new ArrayList<>()).build();
        }

        List<AladinBestSellerResDto> items = aladinResponse.getItems().stream()
                .map(this::convertToBestSellerDto)
                .collect(Collectors.toList());

        // 3. 전체 건수와 함께 래퍼 DTO 반환
        return BestsellerResponse.builder()
                .books(items)
                .totalResults(aladinResponse.getTotalResults()) // 응답 객체에서 꺼냄
                .currentPage(page)
                .itemsPerPage(size)
                .build();
    }

    private AladinBestSellerResDto convertToBestSellerDto(AladinResponse.Item item) {
        // 저자와 번역가 분리 로직 (예: "스즈키 유이 (지은이), 이지수 (옮긴이)")
        String fullAuthor = item.getAuthor();
        String author = fullAuthor;
        String translator = "";

        if (fullAuthor.contains("(옮긴이)")) {
            String[] parts = fullAuthor.split(",");
            author = parts[0].trim();
            translator = parts[1].replace("(옮긴이)", "").trim();
        }

        return AladinBestSellerResDto.builder()
                .bookTitle(item.getTitle())
                .bookAuthor(author)
                .bookTranslator(translator)
                .publisherName(item.getPublisher())
                .publicationDate(item.getPubDate())
                .bookIsbn(item.getIsbn13())
                .bookContents(item.getDescription())
                .bookThumbnail(item.getCover())
                .bestRank(item.getBestRank())
                .bestDuration(item.getBestDuration())
                .customerReviewRank(item.getCustomerReviewRank())
                .build();
    }

}
