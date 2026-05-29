package com.library.bookarte.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifyCodeResponse {
    private Long memberId;
    private String resetToken;
}
