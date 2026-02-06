package com.library.bookarte.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private Long id;
    private String userId;
    private String name;
    private String email;
    private String tel;
    private Long point;
}
