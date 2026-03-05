package com.library.bookarte.board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponse {
    private Long id;
    private String category;
    private String title;
    private String contents;
    private String noticeYn;
    private Long orderNum;
    private Long regMemberId;
    private Long modMemberId;
    private LocalDateTime createDate;
}
