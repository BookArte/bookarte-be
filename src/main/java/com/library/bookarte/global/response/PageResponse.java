package com.library.bookarte.global.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {
    private List<T> content;          // 실제 데이터 리스트
    private int currentPage;          // 현재 페이지 번호
    private int totalPages;           // 전체 페이지 수
    private long totalElements;       // 전체 데이터 개수
    private boolean isFirst;          // 첫 페이지 여부
    private boolean isLast;           // 마지막 페이지 여부

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
