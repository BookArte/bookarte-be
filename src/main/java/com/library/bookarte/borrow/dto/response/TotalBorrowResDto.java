package com.library.bookarte.borrow.dto.response;


import com.library.bookarte.borrow.entity.type.Status;
import com.library.bookarte.member.entity.Member;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
@Getter
@SuperBuilder
public class TotalBorrowResDto extends UserBorrowResDto  {

    //멤버 정보
    private final String memberUserId;
    private final String memberName;
    private final String memberEmail;

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
                             Status status,
                             Member member) {
        super(borrowId, returnDueDate, returnDate, borrowDate, canExtend, isOverdue, overdueDays, status,bookId, bookTitle, bookAuthor);
        this.memberUserId = member.getMemberUserId();
        this.memberName = member.getMemberName();
        this.memberEmail = member.getMemberEmail();
    }
}
