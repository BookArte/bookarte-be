package com.library.bookarte.penalty.entity;

import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.global.base.BaseEntity;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.penalty.dto.PenaltyResDto;
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
public class Penalty extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long penaltyId;

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

    private String lastModifiedBy; // 최종 수정자

    //패널티 해제
    public void releasePenalty(boolean isReleased,
                               String releaseReason,
                               String releasedBy,
                               LocalDateTime releasedAt){
        this.isReleased = isReleased;
        this.releaseReason = releaseReason;
        this.releasedBy = releasedBy;
        this.releasedAt = releasedAt;
        this.lastModifiedBy = releasedBy;
    }

    //해제 취소
    public void cancelRelease(String canceledBy){
        this.isReleased = false;
        this.releaseReason = null;
        this.releasedBy = null;
        this.releasedAt = null;
        this.lastModifiedBy = canceledBy;
    }

    //패널티 해제 사유 변경
    public void updateReason(String releaseReason,
                             String modifiedBy){
        this.releaseReason = releaseReason;
        this.lastModifiedBy = modifiedBy;
    }

    //응답 dto로 변환
    public PenaltyResDto toResDto(){
        return PenaltyResDto.builder()
                .penaltyId(this.penaltyId)
                .penaltyReason(this.penaltyReason)
                .penaltyStartDate(this.startDate)
                .penaltyEndDate(this.endDate)
                .isReleased(this.isReleased)
                .releaseReason(this.releaseReason)
                .memberUserId(this.member.getMemberUserId())
                .memberName(this.member.getMemberName())
                .memberEmail(this.member.getMemberEmail())
                .memberTel(this.member.getMemberTel())
                .bookTitle(this.borrow.getBook().getBookTitle())
                .bookIsbn(this.borrow.getBook().getBookIsbn())
                .borrowStartDate(this.borrow.getCreatedAt().toLocalDate())
                .borrowEndDate(this.borrow.getReturnDueDate())
                .returnDay(this.borrow.getReturnDate())
                .overdueStartDate(this.borrow.getReturnDueDate().plusDays(1))
                .overdueEndDate(this.borrow.getReturnDate())
                .overdueDays(this.borrow.getOverdueDays())
                .releasedBy(this.releasedBy)
                .releasedAt(this.releasedAt)
                .lastModifiedBy(this.lastModifiedBy)
                .lastModifiedAt(this.getUpdatedAt())
                .build();
    }

}
