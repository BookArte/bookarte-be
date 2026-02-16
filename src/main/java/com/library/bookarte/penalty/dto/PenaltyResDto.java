package com.library.bookarte.penalty.dto;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenaltyResDto {
    //패널티 정보
    private Long penaltyId;//패널티 id
    private String penaltyReason; //패널티 사유
    private LocalDate penaltyStartDate; // 패널티시작일
    private LocalDate penaltyEndDate; // 패널티종료일
    private boolean isReleased;// 해제 여부
    private String releaseReason;//해제 사유

    private String releasedBy; //해제 처리한 관리자 ID
    private LocalDateTime releasedAt; //해제 일시

    private String lastModifiedBy; //마지막 수정자
    private LocalDateTime lastModifiedAt; //마지막 수정 일시

    //유저 정보
    private String memberUserId; //닉네임
    private String memberName; //본명
    private String memberTel; //전화번호
    private String memberEmail; //이메일

    //대출 정보
    private String bookTitle; //도서 제목
    private String bookIsbn; //도서 isbn
    private LocalDate borrowStartDate; //대출 시작일
    private LocalDate borrowEndDate; //대출 종료일, 반납 예정일
    private LocalDate returnDay; //반납일

    private LocalDate overdueStartDate; //연체 첫날
    private LocalDate overdueEndDate; //연체 마지막날
    private int overdueDays; //총 연체일수
}
