package com.library.bookarte.book.entity;

import com.library.bookarte.global.base.BaseEntity;
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
@Table(name = "book")
public class Book extends BaseEntity {

    //도서일련번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //도서명, book_title
    @Column(nullable = false)
    private String bookTitle;

    //저자 book_author
    @Column(nullable = false)
    private String bookAuthor;

    //출판사 publisher_name
    @Column(nullable = false)
    private String publisherName;

    //빌헹일 publication_date
    @Column(nullable = false)
    private LocalDate publicationDate;

    //표준번호 book_isbn
    @Column(nullable = false)
    private String bookIsbn;

    //정보 book_contents
    @Column(nullable = false)
    private String bookContents;

    //대출가능여부 book_borrow_yn
    @Column(nullable = false)
    private char bookBorrowYn;

    //청구기호 book_call_number
    @Column(nullable = false)
    private String bookCallNumber;

    //표지사진 book_thumbnail
    @Column(nullable = false)
    private String bookThumbnail;
}
