package com.library.bookarte.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class GlobalResponseDto<T> {

    private final boolean success;
    private final int code;
    private final String status;
    private final String message;
    private final T data;

    /*성공 응답*/
    public static <T> GlobalResponseDto<T> success(HttpStatus status, T data) {
        return GlobalResponseDto.<T>builder()
                .success(true)
                .code(status.value())
                .status(status.name())
                .message("요청 성공")
                .data(data)
                .build();
    }

    /*실패 응답*/
    public static <T> GlobalResponseDto<T> fail(HttpStatus status, T error) {
        return GlobalResponseDto.<T>builder()
                .success(false)
                .code(status.value())
                .status(status.name())
                .message("요청 실패")
                .data(error)
                .build();
    }
}
