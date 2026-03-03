package com.library.bookarte.board.entity;

import com.library.bookarte.board.entity.type.BoardType;
import com.library.bookarte.member.entity.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(BoardType.Constants.NOTICE)
@Table(name = "notice")
public class Notice extends Board {
    @Builder
    public Notice(Long boardId, String category, String title, String contents,
                  Long viewCnt, String noticeYn, Long orderNum, Member member) {
        super(boardId, category, title, contents, viewCnt, noticeYn, orderNum, member);
    }
}
