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
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리는 존재하지 않습니다."),

    //auth
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원정보가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "패스워드가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않는 토큰입니다."),
  
    //recommendation
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 추천 도서는 존재하지 않습니다"),


    //server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
