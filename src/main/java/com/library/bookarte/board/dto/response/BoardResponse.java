package com.library.bookarte.board.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.library.bookarte.board.entity.Board;
import com.library.bookarte.board.entity.Qna;
import com.library.bookarte.board.entity.type.QnaStatus;
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
    private String answerStatus;
    private String admAnswer;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime admAnswerDate;

    public static BoardResponse from(Board board) {
        BoardResponseBuilder builder = BoardResponse.builder()
                .id(board.getBoardId())
                .category(board.getCategory())
                .title(board.getTitle())
                .contents(board.getContents())
                .noticeYn(board.getNoticeYn())
                .orderNum(board.getOrderNum())
                .regMemberUserId(board.getRegMember().getMemberUserId())
                .modMemberUserId(board.getModMember() != null ? board.getModMember().getMemberUserId() : null)
                .createdAt(board.getCreatedAt());

        if (board instanceof Qna qna) {

            String statusDesc = (qna.getQnaStatus() != null)
                    ? qna.getQnaStatus().getDescription()
                    : null;

            builder.answerStatus(statusDesc)
                    .admAnswer(qna.getAdmAnswer())
                    .admAnswerDate(qna.getAdmAnswerDate());
        }

        return builder.build();
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

        BoardResponseBuilder builder = BoardResponse.builder()
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
                .fileList(fileResponses);

        if (board instanceof Qna qna) {

            String statusDesc = (qna.getQnaStatus() != null)
                    ? qna.getQnaStatus().getDescription()
                    : null;

            builder.answerStatus(statusDesc)
                    .admAnswer(qna.getAdmAnswer())
                    .admAnswerDate(qna.getAdmAnswerDate());
        }

        return builder.build();
    }
}
