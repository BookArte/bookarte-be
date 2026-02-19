package com.library.bookarte.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GeminiRequest {
    @NotBlank(message = "내용을 입력해주세요.")
    private String inputMessage;
}
