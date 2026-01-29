package com.library.bookarte.borrow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.library.bookarte.borrow.entity.type.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
public class BorrowSearchFilterDto {

    private Long memberId;

    private Long bookId;

    private Status status;

    private boolean isOverdue;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String searchKeyword;
}
