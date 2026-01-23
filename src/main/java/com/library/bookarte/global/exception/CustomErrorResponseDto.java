package com.library.bookarte.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

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

    //유효성 검증 오류 응답
    public static ResponseEntity<CustomErrorResponseDto> valid(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CustomErrorResponseDto.builder()
                        .success(false)
                        .code(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST.name())
                        .data(errors)
                        .message("요청 실패")
                        .build()
                );
    }
}
