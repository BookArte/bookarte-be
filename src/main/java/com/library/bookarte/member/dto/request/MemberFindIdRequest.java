package com.library.bookarte.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberFindIdRequest {
    @NotBlank
    private String memberName;

    @NotBlank
    private String memberTel;

    @NotBlank
    @Email
    private String memberEmail;
}
