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
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않는 토큰입니다."),
    AUTH_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었거나 유효하지 않은 요청입니다."),
    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "인증 번호가 일치하지 않습니다."),

    //member
    MEMBER_DELETE_STATUS_ERROR(HttpStatus.BAD_REQUEST, "이미 탈퇴된 회원입니다."),
    MEMBER_USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "회원정보가 일치한 아이디가 존재하지 않습니다."),
    MEMBER_NOT_ADMIN(HttpStatus.UNAUTHORIZED, "관리자 권한이 없습니다."),
    MEMBER_NOT_MATCH(HttpStatus.BAD_REQUEST, "회원정보가 일치하지 않습니다."),
  
    //recommendation
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 추천 도서는 존재하지 않습니다"),
    RECOMMENDATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "해당 기간 내 추천 도서는 최대 10권까지만 등록 가능합니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST,"적합하지 않은 날짜 범위입니다"),
    DUPLICATE_RECOMMENDATION_PERIOD(HttpStatus.CONFLICT, "해당 도서는 이미 등록된 기간과 중복됩니다."),

    //server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),

    //borrow
    BOOK_BORROW_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 도서는 이미 대출 중인 도서입니다."),
    NOT_YOUR_BORROW_RECORD(HttpStatus.UNAUTHORIZED,"해당 대출 이력에 대한 권한이 없습니다."),
    INVALID_RETURN_REQUEST(HttpStatus.BAD_REQUEST, "반납 처리 중이거나 반납 완료된 도서입니다."),
    NOT_RETURN_REQUEST(HttpStatus.BAD_REQUEST,"반납 신청되지 않은 도서입니다."),
    CAN_NOT_EXTEND(HttpStatus.BAD_REQUEST, "연장이 불가능한 도서입니다"),
    NOT_STATUS_BORROW(HttpStatus.BAD_REQUEST, "반납 처리 중이거나 연체 중인 도서는 연장 불가능합니다."),
    BORROW_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 대출 내역은 존재하지 않습니다."),
    USER_BORROW_RESTRICTED(HttpStatus.BAD_REQUEST,"연체 중인 도서가 존재하거나 패널티가 존재하여 도서 대출이 불가합니다."),

    //penalty
    PENALTY_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 패널티에 대해서 존재하지 않습니다."),
    ALREADY_RELEASE(HttpStatus.BAD_REQUEST,"이미 해제된 패널티입니다."),
    NOT_RELEASE(HttpStatus.BAD_REQUEST, "해제되지 않은 패널티입니다."),

    //board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."),
    INVALID_BOARD_TYPE(HttpStatus.BAD_REQUEST, "잘못된 타입입니다."),
    //wish
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 관심 도서는 존재하지 않습니다");


    private final HttpStatus httpStatus;
    private final String message;
}
