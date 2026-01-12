package com.library.bookarte.book.entity;

import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.library.bookarte.category.entity.Category;
import com.library.bookarte.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "book")
public class Book extends BaseEntity {

    //도서일련번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    //도서명, book_title
    @Column(nullable = false)
    private String bookTitle;

    //저자,역자
    @ElementCollection
    @CollectionTable(
            name = "book_participant",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @OrderBy("name ASC")
    private List<Participant> participants = new ArrayList<>();

    /*
    //저자 book_author
    @Column(nullable = false)
    private String bookAuthor;

    //역자
    @Column
    private String bookTranslator;
     */

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

    @Builder
    public Book(String bookTitle,
                String publisherName,
                LocalDate publicationDate,
                String bookIsbn,
                String bookContents,
                char bookBorrowYn,
                String bookCallNumber,
                String bookThumbnail,
                Category category) {
        this.bookTitle = bookTitle;
        this.publisherName = publisherName;
        this.publicationDate = publicationDate;
        this.bookIsbn = bookIsbn;
        this.bookContents = bookContents;
        this.bookBorrowYn = bookBorrowYn;
        this.bookCallNumber = bookCallNumber;
        this.bookThumbnail = bookThumbnail;
        this.category = category;
    }

    public void addParticipant(String name, ParticipantType type) {
        if (name != null && !name.trim().isEmpty()) {
            this.participants.add(new Participant(name.trim(), type));
        }
    }

    public void updateBook(String bookTitle,
                           String publisherName,
                           LocalDate publicationDate,
                           String bookIsbn,
                           String bookContents,
                           String bookCallNumber,
                           String bookThumbnail,
                           Category category,
                           List<Participant> newParticipants){

        this.bookTitle = bookTitle;
        this.publisherName = publisherName;
        this.publicationDate = publicationDate;
        this.bookIsbn = bookIsbn;
        this.bookContents = bookContents;
        this.bookCallNumber = bookCallNumber;
        this.bookThumbnail = bookThumbnail;
        this.category = category;

        if (this.participants != newParticipants && newParticipants != null) {
            this.participants.clear();
            this.participants.addAll(newParticipants);
        }
    }

    public BookResDto toBookResDto(){
        String authors = this.participants.stream()
                .filter(p -> p.getType() == ParticipantType.AUTHOR)
                .map(Participant::getName)
                .collect(Collectors.joining(", "));

        String translators = this.participants.stream()
                .filter(p -> p.getType() == ParticipantType.TRANSLATOR)
                .map(Participant::getName)
                .collect(Collectors.joining(", "));

        return  BookResDto.builder()
                .bookId(this.bookId)
                .bookTitle(this.bookTitle)
                .bookAuthor(authors)
                .bookTranslator(translators)
                .publisherName(this.publisherName)
                .publicationDate(this.publicationDate)
                .bookContents(this.bookContents)
                .bookCallNumber(this.bookCallNumber)
                .bookIsbn(this.bookIsbn)
                .bookThumbnail(this.bookThumbnail)
                .bookCategoryName(this.category.getCategoryName())
                .build();
    }

    /**
     * 저자/역자 정보를 담는 값 타입
     */
    @Embeddable
    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class Participant {
        @Column(nullable = false)
        private String name;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ParticipantType type; // AUTHOR, TRANSLATOR
    }
}



