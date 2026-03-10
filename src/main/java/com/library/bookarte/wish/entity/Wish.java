package com.library.bookarte.wish.entity;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.utils.BookParticipantUtils;
import com.library.bookarte.global.base.BaseEntity;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.wish.dto.response.WishResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wish")
public class Wish extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "book_id")
    private Book book;

    public WishResDto toWishResDto(){
        String authors = BookParticipantUtils.extractAuthors(this.book.getParticipants());

        String translators = BookParticipantUtils.extractTranslators(this.book.getParticipants());

        return WishResDto.builder()
                .wishId(this.wishId)
                .bookId(this.book.getBookId())
                .bookTitle(this.book.getBookTitle())
                .bookIsbn(this.book.getBookIsbn())
                .bookAuthor(authors)
                .bookTranslator(translators)
                .bookThumbnail(this.book.getBookThumbnail())
                .build();
    }

}
