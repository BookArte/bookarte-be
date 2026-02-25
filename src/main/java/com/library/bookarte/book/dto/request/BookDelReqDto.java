package com.library.bookarte.book.dto.request;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookDelReqDto {
    List<Long> bookIds;
}
