package com.library.bookarte.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CursorResponse<T> {
    private List<T> content;     // 조회된 데이터 리스트
    private Long lastCursor;     // 다음 조회를 위한 마지막 ID
    private boolean hasNext;     // 다음 페이지 존재 여부
}
