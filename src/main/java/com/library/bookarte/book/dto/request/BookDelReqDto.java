package com.library.bookarte.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookDelReqDto {

    @NotBlank(message = "도서 삭제 사유는 필수 입력 항목입니다.")
    String delReason;
}
