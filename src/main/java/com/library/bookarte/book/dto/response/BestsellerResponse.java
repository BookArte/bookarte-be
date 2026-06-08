package com.library.bookarte.book.dto.response;

import com.library.bookarte.book.external.dto.AladinBestSellerResDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BestsellerResponse {
    private List<AladinBestSellerResDto> books;
    private int totalResults;
    private int currentPage;
    private int itemsPerPage;
}
