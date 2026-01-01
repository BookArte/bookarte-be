package com.library.bookarte.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdCheckResponse {
    private String userId;
    private boolean available;
}
