package com.library.bookarte.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDeleteRequest {
    private String password;
    private String reason;
}
