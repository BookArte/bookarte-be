package com.library.bookarte.book.external.national.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

@Getter
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class NationalLibraryCategoryResponse {

    @XmlElement(name = "result")
    private Result result;

    public String getCategory() {
        if (result != null && result.getItem() != null) {
            return result.getItem().getCategory();
        }
        return null;
    }

    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Result {
        @XmlElement(name = "item")
        private Item item;
    }

    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Item {
        @XmlElement(name = "kdc_name_1s")
        private String category;
    }
}