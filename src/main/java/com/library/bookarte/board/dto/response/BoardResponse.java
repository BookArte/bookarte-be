package com.library.bookarte.board.dto.response;

import com.library.bookarte.board.entity.Board;
import com.library.bookarte.global.entity.UploadFile;
import com.library.bookarte.global.entity.type.FileType;
import com.library.bookarte.global.response.FileResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private String thumbnailPath;
    private List<FileResponse> fileList;

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

    public static BoardResponse from(Board board, List<UploadFile> files) {

        String thumb = files.stream()
                .filter(f -> FileType.Constants.THUMBNAIL.equals(f.getFileRole()))
                .map(UploadFile::getFileUrl)
                .findFirst()
                .orElse(null);

        List<FileResponse> fileResponses = files.stream()
                .filter(f -> FileType.Constants.FILE.equals(f.getFileRole()))
                .map(FileResponse::from)
                .collect(Collectors.toList());

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
                .thumbnailPath(thumb)
                .fileList(fileResponses)
                .build();
    }
}
