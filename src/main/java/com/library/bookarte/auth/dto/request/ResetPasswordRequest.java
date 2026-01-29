package com.library.bookarte.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotNull(message = "사용자 식별값이 누락되었습니다.")
    private Long id;

    @NotBlank(message = "인증 토큰이 누락되었습니다.")
    private String resetToken;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",
            message = "비밀번호는 영문자와 숫자를 포함하여 8~16자로 입력해주세요.")
    private String memberPassword;
}
