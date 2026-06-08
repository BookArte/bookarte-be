package com.library.bookarte.book.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.utils.BookParticipantUtils;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResDto {
    private Long bookId;

    private String bookTitle;

    private String bookAuthor;

    private String publisherName;

    private String bookTranslator;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    private String bookIsbn;

    private String bookContents;

    private String bookThumbnail;

    private String bookCallNumber;

    private String bookCategory;

    private boolean canBorrow;

    private boolean isWish;

    private int wishCount;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    public BookResDto(Long bookId,
                      String bookTitle,
                      List<Book.Participant> participants,
                      String publisherName,
                      LocalDate publicationDate,
                      String bookIsbn,
                      String bookContents,
                      String bookThumbnail,
                      String bookCallNumber,
                      String bookCategory,
                      boolean canBorrow,
                      boolean isWish,
                      LocalDateTime createdAt,
                      LocalDateTime lastUpdatedAt) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.publisherName = publisherName;
        this.publicationDate = publicationDate;
        this.bookIsbn = bookIsbn;
        this.bookContents = bookContents;
        this.bookThumbnail = bookThumbnail;
        this.bookCallNumber = bookCallNumber;
        this.bookCategory = bookCategory;
        this.canBorrow = canBorrow;
        this.isWish = isWish;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;

        // 리스트를 순회하며 저자와 역자를 콤마(,)로 구분된 문자열로 변환
        if (participants != null) {
            this.bookAuthor = BookParticipantUtils.extractAuthors(participants);
            this.bookTranslator = BookParticipantUtils.extractTranslators(participants);
        }
    }
}
