package com.library.bookarte.borrow.dto;


import com.library.bookarte.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TotalBorrowResDto extends UserBorrowResDto  {

    //멤버 정보
    private final String memberUserId;
    private final String memberName;
    private final String memberEmail;

    @Builder
    public TotalBorrowResDto(Long borrowId,
                             LocalDate returnDueDate,
                             LocalDate returnDate,
                             LocalDate borrowDate,
                             boolean canExtend,
                             boolean isOverdue,
                             int overdueDays,
                             Long bookId,
                             String bookTitle,
                             String bookAuthor,
                             Member member) {
        super(borrowId, returnDueDate, returnDate, borrowDate, canExtend, isOverdue, overdueDays, bookId, bookTitle, bookAuthor);
        this.memberUserId = member.getMemberUserId();
        this.memberName = member.getMemberName();
        this.memberEmail = member.getMemberEmail();
    }



}
