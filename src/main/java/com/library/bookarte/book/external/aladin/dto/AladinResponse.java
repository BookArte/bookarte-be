package com.library.bookarte.book.external.aladin.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "object")
public class AladinResponse {

    private List<Item> items;

    @XmlElement(name = "item")
    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Data
    public static class Item {
        private String title;
        private String author;
        private String publisher;
        private String pubDate;
        private String isbn13;
        private String description;
        private String cover;
        private int bestRank;
        private String bestDuration;
        private int customerReviewRank;

        // JAXB는 필드명이 XML 태그와 다를 경우 @XmlElement로 매핑합니다.
        // 태그명이 같은 경우 생략 가능하지만 명시해주는 것이 안전합니다.
        @XmlElement(name = "title") public void setTitle(String title) { this.title = title; }
        @XmlElement(name = "author") public void setAuthor(String author) { this.author = author; }
        @XmlElement(name = "publisher") public void setPublisher(String publisher) { this.publisher = publisher; }
        @XmlElement(name = "pubDate") public void setPubDate(String pubDate) { this.pubDate = pubDate; }
        @XmlElement(name = "isbn13") public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }
        @XmlElement(name = "description") public void setDescription(String description) { this.description = description; }
        @XmlElement(name = "cover") public void setCover(String cover) { this.cover = cover; }
        @XmlElement(name = "bestRank") public void setBestRank(int bestRank) { this.bestRank = bestRank; }
        @XmlElement(name = "bestDuration") public void setBestDuration(String bestDuration) { this.bestDuration = bestDuration; }
        @XmlElement(name = "customerReviewRank") public void setCustomerReviewRank(int customerReviewRank) { this.customerReviewRank = customerReviewRank; }
    }

}
