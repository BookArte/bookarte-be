package com.library.bookarte.wish.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishResDto {
    private Long wishId;

    //도서 정보
    private Long bookId;

    private String bookTitle;

    private String bookIsbn;

    private String bookAuthor;

    private String bookTranslator;

    private String bookThumbnail;

    private String publisherName;

    private String category;
}
