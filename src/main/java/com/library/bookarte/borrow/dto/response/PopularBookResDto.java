package com.library.bookarte.borrow.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularBookResDto {
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookThumbnail;
    private String bookTranslator;
    private String publisherName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;
    private String bookCategory;
    private String bookIsbn;
    private Long borrowCount;

}
