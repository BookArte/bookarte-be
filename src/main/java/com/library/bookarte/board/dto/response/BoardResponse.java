package com.library.bookarte.board.dto.response;

import com.library.bookarte.board.entity.Board;
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
    private String regMemberUserId;
    private String modMemberUserId;
    private LocalDateTime createdAt;

    public static BoardResponse from(Board board) {
        return BoardResponse.builder()
                .id(board.getBoardId())
                .category(board.getCategory())
                .title(board.getTitle())
                .contents(board.getContents())
                .noticeYn(board.getNoticeYn())
                .orderNum(board.getOrderNum())
                .regMemberUserId(board.getRegMember().getMemberUserId())
                .modMemberUserId(board.getModMember() != null ? board.getModMember().getMemberUserId() : null)
                .createdAt(board.getCreatedAt())
                .build();
    }
}
