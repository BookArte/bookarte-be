package com.library.bookarte.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BulkDeleteResponse {
    private int totalRequestCount;
    private int skippedCount;
    private int deletedCount;
    private List<String> skippedTitles;
}
