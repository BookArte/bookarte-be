package com.library.bookarte.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class CustomErrorResponseDto {

    private final boolean success;
    private final int code;
    private final String status;
    private final String message;
    private final String data;

    @Builder
    public CustomErrorResponseDto(boolean success, int code, String status, String message, String data) {
        this.success = success;
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    //에러 응답
    public static ResponseEntity<CustomErrorResponseDto> toResponseEntity(CustomErrorCode customErrorCode) {
        return ResponseEntity
                .status(customErrorCode.getHttpStatus())
                .body(CustomErrorResponseDto.builder()
                        .success(false)
                        .code(customErrorCode.getHttpStatus().value())
                        .status(customErrorCode.name())
                        .data(customErrorCode.getMessage())
                        .message("요청 실패")
                        .build()
                );
    }
}
