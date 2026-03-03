package com.library.bookarte.board.entity;

import com.library.bookarte.global.base.BaseEntity;
import com.library.bookarte.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = true)
    private String category;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contents;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCnt = 0L;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String noticeYn;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long orderNum = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    protected Board(Long boardId, String category, String title, String contents,
                    Long viewCnt, String noticeYn, Long orderNum, Member member) {
        this.boardId = boardId;
        this.category = category;
        this.title = title;
        this.contents = contents;
        this.viewCnt = viewCnt != null ? viewCnt : 0L;
        this.noticeYn = noticeYn != null ? noticeYn : "N";
        this.orderNum = orderNum != null ? orderNum : 0L;
        this.member = member;
    }
}
