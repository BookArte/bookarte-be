package com.library.bookarte.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardUpdateRequest {
    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    @NotBlank
    private String noticeYn;

    @NotBlank
    private Long orderNum;
}
