package com.library.bookarte.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {
    @NotBlank
    private Long id;

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
}
