package com.library.bookarte.board.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QnaStatus {
    WAITING("답변 대기"),
    COMPLETED("답변 완료");

    private final String description;
}
