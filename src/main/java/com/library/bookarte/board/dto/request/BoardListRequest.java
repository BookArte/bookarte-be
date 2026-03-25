package com.library.bookarte.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BoardListRequest {
    private int page = 0;
    private int size = 10;
    private String searchText;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchStartDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchEndDate;
}
