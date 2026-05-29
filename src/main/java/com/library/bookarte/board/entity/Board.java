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
    @JoinColumn(name = "reg_member_id", nullable = false)
    private Member regMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_member_id", nullable = true)
    private Member modMember;

    protected Board(Long boardId, String category, String title, String contents,
                    Long viewCnt, String noticeYn, Long orderNum, Member regMember, Member modMember) {
        this.boardId = boardId;
        this.category = category;
        this.title = title;
        this.contents = contents;
        this.viewCnt = viewCnt != null ? viewCnt : 0L;
        this.noticeYn = noticeYn != null ? noticeYn : "N";
        this.orderNum = orderNum != null ? orderNum : 0L;
        this.regMember = regMember;
        this.modMember = modMember;
    }

    protected void updateBoard(String category, String title, String contents, String noticeYn, Long orderNum, Member modMember) {
        if (category != null) this.category = category;
        if (title != null) this.title = title;
        if (contents != null) this.contents = contents;
        if (noticeYn != null) this.noticeYn = noticeYn;
        if (orderNum != null) this.orderNum = orderNum;
        if (modMember != null) this.modMember = modMember;
    }
}
