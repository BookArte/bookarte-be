package com.library.bookarte.penalty.dto;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenaltyResDto {
    //패널티 정보
    private String penaltyReason; //패널티 사유
    private LocalDate startDate; // 시작일
    private LocalDate endDate; // 종료일

    private boolean isReleased;// 해제 여부
    private String releaseReason;//해제 사유

    //유저 정보
    private String memberUserId; //닉네임
    private String memberName; //본명
    private String memberTel; //전화번호
    private String memberEmail; //이메일

    //대출 정보
    private String bookTitle;
    private String bookIsbn;
    private int overdueDays;
}
