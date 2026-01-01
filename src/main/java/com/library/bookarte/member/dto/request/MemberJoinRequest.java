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
    private String userId;

    @NotBlank
    private String name;

    @NotBlank
    private String tel;

    @NotBlank
    @Size(min = 8, max = 16)
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private Boolean agreeService;

    @NotBlank
    private Boolean agreePrivacy;
}
