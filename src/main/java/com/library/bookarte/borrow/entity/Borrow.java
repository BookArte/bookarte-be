package com.library.bookarte.borrow.entity;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.library.bookarte.borrow.dto.response.TotalBorrowResDto;
import com.library.bookarte.borrow.dto.response.UserBorrowResDto;
import com.library.bookarte.borrow.entity.type.Status;
import com.library.bookarte.global.base.BaseEntity;
import com.library.bookarte.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "borrow")
public class Borrow extends BaseEntity {

    //대출일련번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long borrowId;

    //반납예정일
    private LocalDate returnDueDate;

    //반납일
    private LocalDate returnDate;

    //연장 여부
    private boolean canExtend;

    //연체 여부
    private boolean isOverdue;

    //연체 일수
    private int overdueDays;

    //대출 상태
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public TotalBorrowResDto toTotalBorrowResDto(){
        String authors = this.book.getParticipants().stream()
                .filter(participant -> participant.getType() == ParticipantType.AUTHOR)
                .map(Book.Participant::getName)
                .collect(Collectors.joining(", "));

        return TotalBorrowResDto.builder()
                .borrowId(this.borrowId)
                .returnDate(this.returnDate)
                .returnDueDate(this.returnDueDate)
                .borrowDate(LocalDate.from(this.getCreatedAt()))
                .canExtend(this.canExtend)
                .isOverdue(this.isOverdue)
                .overdueDays(this.overdueDays)
                .status(this.status)
                .bookId(this.book.getBookId())
                .bookTitle(this.book.getBookTitle())
                .bookAuthor(authors)
                .memberUserId(this.member.getMemberUserId())
                .memberName(this.member.getMemberName())
                .memberEmail(this.member.getMemberEmail())
                .build();
    }

    public UserBorrowResDto toUserBorrowResDto(){
        String authors = this.book.getParticipants().stream()
                .filter(participant -> participant.getType() == ParticipantType.AUTHOR)
                .map(Book.Participant::getName)
                .collect(Collectors.joining(", "));

        return UserBorrowResDto.builder()
                .borrowId(this.borrowId)
                .returnDate(this.returnDate)
                .returnDueDate(this.returnDueDate)
                .borrowDate(LocalDate.from(this.getCreatedAt()))
                .canExtend(this.canExtend)
                .isOverdue(this.isOverdue)
                .overdueDays(this.overdueDays)
                .status(this.status)
                .bookId(this.book.getBookId())
                .bookTitle(this.book.getBookTitle())
                .bookAuthor(authors)
                .build();
    }

    public void updateStatus(Status status){
        this.status = status;
    }
}
