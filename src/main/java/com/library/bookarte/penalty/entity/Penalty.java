package com.library.bookarte.penalty.entity;

import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_id")
    private Borrow borrow;

    private LocalDate startDate;
    private LocalDate endDate;

    private String penaltyReason;

    //관리자 권한으로 연체 패널티 수도 해제
    private boolean isReleased;        // 해제 여부
    private String releaseReason;      // "병가 참작", "시스템 오류로 인한 오부과" 등
    private String releasedBy;         // 해제 처리한 관리자 ID
    private LocalDateTime releasedAt;  // 해제 처리 일시

}
