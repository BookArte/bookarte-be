package com.library.bookarte.book.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReqDto {

    //책제목
    @NotBlank(message = "도서 제목은 필수 입력 항목입니다.")
    private String bookTitle;

    //저자
    @NotBlank(message = "저자는 필수 입력 항목입니다.")
    private String bookAuthor;

    //역자
    @Size(max = 100, message = "번역가 이름은 100자 이내여야 합니다.")
    private String bookTranslator;

    //출판사
    @NotBlank(message = "출판사는 필수 입력 항목입니다.")
    private String publisherName;

    //출판일
    @NotNull(message = "출판일은 필수 입력 항목입니다.")
    @PastOrPresent(message = "출판일은 미래 날짜일 수 없습니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    //ISBN
    @NotBlank(message = "ISBN은 필수 입력 항목입니다.")
    private String bookIsbn;

    //책소개
    @NotBlank(message = "도서 소개는 필수 입력 항목입니다.")
    private String bookContents;

    //썸네일(url or 파일 선택)
    private String bookThumbnail;
    private MultipartFile bookThumbnailFile;

    //청구기호
    //현재 실제 있는 도서관에 대한 사이트가 아니기에 청구기호는 비어둠
    private String bookCallNumber;

    //카테고리
    @NotBlank(message = "카테고리는 필수 입력 항목입니다.")
    private String bookCategory;

}
