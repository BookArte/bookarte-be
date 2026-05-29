package com.library.bookarte.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberJoinResponse {
    private Long id;
    private String userId;
    private String name;
    private String email;
}
