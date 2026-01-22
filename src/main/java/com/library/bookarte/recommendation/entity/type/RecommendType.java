package com.library.bookarte.recommendation.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecommendType {
    MAIN("MAIN","메인"),
    ADMIN_PICK("ADMIN_PICK","관리자 추천"),
    MONTHLY("MONTHLY","월간추천"),
    WEEKLY("WEEKLY","주간추천")
    ;

    private final String key;
    private final String value;
}
