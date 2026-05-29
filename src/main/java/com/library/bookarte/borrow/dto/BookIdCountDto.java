package com.library.bookarte.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookIdCountDto {
    private Long bookId;
    private Long borrowCount;
}
