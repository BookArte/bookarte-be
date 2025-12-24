package com.library.bookarte.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberUpdateResponse {
    private String name;
    private String tel;
    private String email;
}
