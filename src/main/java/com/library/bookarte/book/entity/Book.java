package com.library.bookarte.book.entity;

import com.library.bookarte.book.dto.response.BookResDto;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.library.bookarte.book.utils.BookParticipantUtils;
import com.library.bookarte.borrow.dto.response.PopularBookResDto;
import com.library.bookarte.category.entity.Category;
import com.library.bookarte.global.base.BaseEntity;
import com.library.bookarte.recommendation.entity.Recommendation;
import com.library.bookarte.wish.entity.Wish;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "book")
public class Book extends BaseEntity {

    //도서일련번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    //낙관적 락을 위한 엔티티 버전
/*    @Version
    private Long version;*/

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
    @Column(nullable = false, columnDefinition = "TEXT")
    private String bookContents;

    //대출가능여부 book_borrow_yn
    @Column(nullable = false)
    private boolean canBorrow;

    //청구기호 book_call_number
    @Column
    private String bookCallNumber;

    //표지사진 book_thumbnail
    @Column
    private String bookThumbnail;

    //도서 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Wish> wishes = new ArrayList<>();

    @Builder
    public Book(String bookTitle,
                String publisherName,
                LocalDate publicationDate,
                String bookIsbn,
                String bookContents,
                boolean canBorrow,
                String bookCallNumber,
                String bookThumbnail,
                Category category) {
        this.bookTitle = bookTitle;
        this.publisherName = publisherName;
        this.publicationDate = publicationDate;
        this.bookIsbn = bookIsbn;
        this.bookContents = bookContents;
        this.canBorrow = canBorrow;
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

    public void updateThumbnail(String bookThumbnail){
        this.bookThumbnail = bookThumbnail;
    }

    public BookResDto toBookResDto(){
        String authors = BookParticipantUtils.extractAuthors(this.getParticipants());

        String translators = BookParticipantUtils.extractTranslators(this.getParticipants());

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
                .bookCategory(this.category.getCategoryName())
                .canBorrow(this.canBorrow)
                .build();
    }

    public PopularBookResDto toPopularBookResDto(Long borrowCount){
        String authors = BookParticipantUtils.extractAuthors(this.getParticipants());

        String translators = BookParticipantUtils.extractTranslators(this.getParticipants());


        return PopularBookResDto.builder()
                .bookId(this.bookId)
                .bookTitle(this.bookTitle)
                .publisherName(this.publisherName)
                .publicationDate(this.publicationDate)
                .bookAuthor(authors)
                .bookTranslator(translators)
                .bookCategory(this.category.getCategoryName())
                .bookIsbn(this.bookIsbn)
                .bookThumbnail(this.bookThumbnail)
                .borrowCount(borrowCount)
                .build();
    }


    /* 대출가능여부 업데이트 */
    public void updateCanBorrow(boolean canBorrow){
        this.canBorrow = canBorrow;
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



