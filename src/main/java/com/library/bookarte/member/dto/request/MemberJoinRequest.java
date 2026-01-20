package com.library.bookarte.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberJoinRequest {

    @NotBlank
    private String memberUserId;

    @NotBlank
    private String memberName;

    @NotBlank
    private String memberTel;

    @NotBlank
    @Size(min = 8, max = 16)
    private String memberPassword;

    @NotBlank
    @Email
    private String memberEmail;

    @NotBlank
    private Boolean agreeService;

    @NotBlank
    private Boolean agreePrivacy;
}
