package com.library.bookarte.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberFindPasswordRequest {
    @NotBlank
    private String memberUserId;

    @NotBlank
    private String memberName;

    @NotBlank
    @Email
    private String memberEmail;
}
