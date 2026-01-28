package com.library.bookarte.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyCodeRequest {
    @NotBlank
    private Long memberId;

    @NotBlank
    private String code;

}
