package com.library.bookarte.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberFindPasswordResponse {
    private long expiresIn;
}
