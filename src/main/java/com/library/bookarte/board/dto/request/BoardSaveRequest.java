package com.library.bookarte.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardSaveRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    private String noticeYn;

    private Long orderNum;
}
