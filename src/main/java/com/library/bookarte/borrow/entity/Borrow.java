package com.library.bookarte.borrow.entity;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.borrow.entity.type.Status;
import com.library.bookarte.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "borrow")
public class Borrow {

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
}
