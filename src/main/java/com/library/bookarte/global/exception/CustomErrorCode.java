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
    AUTH_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었거나 유효하지 않은 요청입니다."),
    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "인증 번호가 일치하지 않습니다."),

    //member
    MEMBER_DELETE_STATUS_ERROR(HttpStatus.BAD_REQUEST, "이미 탈퇴된 회원입니다."),
    MEMBER_USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "회원정보가 일치한 아이디가 존재하지 않습니다."),
  
    //recommendation
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 추천 도서는 존재하지 않습니다"),
    RECOMMENDATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "추천 도서는 최대 10권까지만 등록 가능합니다."),

    //server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다");



    private final HttpStatus httpStatus;
    private final String message;
}
