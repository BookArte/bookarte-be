package com.library.bookarte.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class CustomErrorResponseDto {

    private final int statusCode;
    private final String codeName;
    private final String message;

    @Builder
    public CustomErrorResponseDto(int statusCode, String codeName, String message) {
        this.statusCode = statusCode;
        this.codeName = codeName;
        this.message = message;
    }

    //에러 응답
    public static ResponseEntity<CustomErrorResponseDto> toResponseEntity(CustomErrorCode customErrorCode) {
        return ResponseEntity
                .status(customErrorCode.getHttpStatus())
                .body(CustomErrorResponseDto.builder()
                        .statusCode(customErrorCode.getHttpStatus().value())
                        .codeName(customErrorCode.name())
                        .message(customErrorCode.getMessage())
                        .build()
                );
    }
}
