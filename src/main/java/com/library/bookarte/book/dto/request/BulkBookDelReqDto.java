package com.library.bookarte.book.dto.request;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class BulkBookDelReqDto {
    List<Long> bookIds;
}
