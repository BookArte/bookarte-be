package com.library.bookarte.book.external.aladin;

import com.library.bookarte.book.dto.response.BestsellerResponse;
import com.library.bookarte.book.external.aladin.dto.AladinResponse;
import com.library.bookarte.book.external.dto.AladinBestSellerResDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AladinClient {

    private static final String TRANSLATOR_SUFFIX = "(\uC62E\uAE34\uC774)";

    private final RestTemplate restTemplate;

    @Value("${aladin.api.url}")
    private String aladinApiUrl;

    @Value("${aladin.api.key}")
    private String aladinApiKey;

    public BestsellerResponse getBestSellers(String type, int page, int size) {
        URI uri = UriComponentsBuilder.fromUriString(aladinApiUrl)
                .queryParam("ttbkey", aladinApiKey)
                .queryParam("QueryType", type)
                .queryParam("MaxResults", size)
                .queryParam("start", page)
                .queryParam("SearchTarget", "Book")
                .queryParam("output", "xml")
                .queryParam("Version", "20131101")
                .build(true)
                .toUri();

        AladinResponse aladinResponse = requestAladinResponse(uri);

        if (aladinResponse == null || aladinResponse.getItems() == null) {
            return emptyResponse(page, size);
        }

        List<AladinBestSellerResDto> items = aladinResponse.getItems().stream()
                .map(this::convertToBestSellerDto)
                .collect(Collectors.toList());

        return BestsellerResponse.builder()
                .books(items)
                .totalResults(aladinResponse.getTotalResults())
                .currentPage(page)
                .itemsPerPage(size)
                .build();
    }

    private AladinResponse requestAladinResponse(URI uri) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            String body = response.getBody();

            if (body == null || body.isBlank()) {
                log.warn("Aladin API returned an empty response. status={}, contentType={}",
                        response.getStatusCode(), response.getHeaders().getContentType());
                return null;
            }

            if (isHtml(body)) {
                log.warn("Aladin API returned HTML instead of XML. status={}, contentType={}, bodySnippet={}",
                        response.getStatusCode(), response.getHeaders().getContentType(), snippet(body));
                return null;
            }

            return parseXml(body);
        } catch (Exception e) {
            log.warn("Failed to request or parse Aladin API response. message={}", e.getMessage(), e);
            return null;
        }
    }

    private AladinResponse parseXml(String body) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(AladinResponse.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (AladinResponse) unmarshaller.unmarshal(new StringReader(body));
    }

    private boolean isHtml(String body) {
        String normalized = body.stripLeading().toLowerCase(Locale.ROOT);
        return normalized.startsWith("<!doctype html")
                || normalized.startsWith("<html")
                || normalized.contains("<body");
    }

    private String snippet(String body) {
        String normalized = body.replaceAll("\\s+", " ").trim();
        return normalized.substring(0, Math.min(normalized.length(), 300));
    }

    private BestsellerResponse emptyResponse(int page, int size) {
        return BestsellerResponse.builder()
                .books(new ArrayList<>())
                .totalResults(0)
                .currentPage(page)
                .itemsPerPage(size)
                .build();
    }

    private AladinBestSellerResDto convertToBestSellerDto(AladinResponse.Item item) {
        String fullAuthor = item.getAuthor();
        String author = fullAuthor == null ? "" : fullAuthor;
        String translator = "";

        if (fullAuthor != null && fullAuthor.contains(TRANSLATOR_SUFFIX)) {
            String[] parts = fullAuthor.split(",");
            author = parts[0].trim();
            if (parts.length > 1) {
                translator = parts[1].replace(TRANSLATOR_SUFFIX, "").trim();
            }
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
