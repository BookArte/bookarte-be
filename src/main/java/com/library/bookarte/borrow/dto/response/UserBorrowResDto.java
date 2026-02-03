package com.library.bookarte.borrow.dto.response;

import com.library.bookarte.borrow.entity.type.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@SuperBuilder
public class UserBorrowResDto {
    //대출 정보
    private final Long borrowId;
    private final LocalDate returnDueDate; //반납 예정일
    private final LocalDate returnDate; //실제 반납일
    private final LocalDate borrowDate; //대출일
    private final boolean canExtend; //연장 가능 여부
    private final boolean isOverdue; // 연체 여부
    private final int overdueDays; // 연체 일수
    private final Status status;

    //도서 정보
    private final Long bookId;
    private final String bookTitle; //책제목
    private final String bookAuthor; //책저자

}