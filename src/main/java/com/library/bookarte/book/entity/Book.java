package com.library.bookarte.book.entity;

import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.category.entity.Category;
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
    private Long bookId;

    //도서명, book_title
    @Column(nullable = false)
    private String bookTitle;

    //저자 book_author
    @Column(nullable = false)
    private String bookAuthor;

    //번역가
    @Column
    private String bookTranslator;

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
    @Column
    private String bookCallNumber;

    //표지사진 book_thumbnail
    @Column(nullable = false)
    private String bookThumbnail;

    //도서 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public void updateBook(String bookTitle,
                           String bookAuthor,
                           String publisherName,
                           LocalDate publicationDate,
                           String bookIsbn,
                           String bookContents,
                           String bookCallNumber,
                           String bookThumbnail){

        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.publisherName = publisherName;
        this.publicationDate = publicationDate;
        this.bookIsbn = bookIsbn;
        this.bookContents = bookContents;
        this.bookCallNumber = bookCallNumber;
        this.bookThumbnail = bookThumbnail;
    }

    public BookResDto toBookResDto(){
        return  BookResDto.builder()
                .bookId(this.bookId)
                .bookTitle(this.bookTitle)
                .bookAuthor(this.bookAuthor)
                .publisherName(this.publisherName)
                .publicationDate(this.publicationDate)
                .bookContents(this.bookContents)
                .bookCallNumber(this.bookCallNumber)
                .bookIsbn(this.bookIsbn)
                .bookThumbnail(this.bookThumbnail)
                .bookCategoryName(this.category.getCategoryName())
                .build();
    }
}
