package com.library.bookarte.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {

    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "데이터 무결성 오류가 발생했습니다."),

    //book
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 책은 존재하지않습니다"),

    //category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리는 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
