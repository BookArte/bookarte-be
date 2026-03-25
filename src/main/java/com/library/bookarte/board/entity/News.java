package com.library.bookarte.board.entity;

import com.library.bookarte.board.dto.request.BoardUpdateRequest;
import com.library.bookarte.board.entity.type.BoardType;
import com.library.bookarte.member.entity.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(BoardType.Constants.NEWS)
@Table(name = "news")
public class News extends Board {
    @Builder
    public News(Long boardId, String category, String title, String contents,
                Long viewCnt, String noticeYn, Long orderNum, Member regMember, Member modMember) {
        super(boardId, category, title, contents, viewCnt, noticeYn, orderNum, regMember, modMember);
    }

    public void modify(BoardUpdateRequest request, Member modMember) {

        super.updateBoard(
                request.getCategory(),
                request.getTitle(),
                request.getEditor(),
                request.getNoticeYn(),
                request.getOrderNum(),
                modMember
        );

    }
}
