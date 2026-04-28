package com.library.bookarte.board.entity;

import com.library.bookarte.board.dto.request.BoardUpdateRequest;
import com.library.bookarte.board.entity.type.BoardType;
import com.library.bookarte.board.entity.type.QnaStatus;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.entity.type.MemberType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(BoardType.Constants.QNA)
@Table(name = "qna")
public class Qna extends Board {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QnaStatus qnaStatus = QnaStatus.WAITING;

    @Column(nullable = true, columnDefinition = "LONGTEXT")
    private String admAnswer;

    @Column(nullable = true)
    private LocalDateTime admAnswerDate;

    @Builder
    public Qna(Long boardId, String category, String title, String contents,
               Long viewCnt, String noticeYn, Long orderNum, Member regMember, Member modMember) {
        super(boardId, category, title, contents, viewCnt, noticeYn, orderNum, regMember, modMember);

        this.qnaStatus = (qnaStatus != null) ? qnaStatus : QnaStatus.WAITING;
    }

    public void modify(BoardUpdateRequest request, Member modMember) {

        super.updateBoard(
                request.getCategory(),
                request.getTitle(),
                request.getContents(),
                request.getNoticeYn(),
                request.getOrderNum(),
                modMember
        );

        if (MemberType.Constants.ROLE_ADMIN.equals(modMember.getMemberRole())) {
            if (request.getAdmAnswer() != null && !request.getAdmAnswer().isBlank()) {
                completeAnswer(request.getAdmAnswer());
            }
        }
    }

    public void completeAnswer(String contents) {
        this.admAnswer = contents;
        this.admAnswerDate = LocalDateTime.now();
        this.qnaStatus = QnaStatus.COMPLETED;
    }
}
