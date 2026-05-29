package com.library.bookarte.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    private String memberUserId;

    @NotBlank
    private String memberPassword;
}
