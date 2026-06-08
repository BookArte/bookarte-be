package com.library.bookarte.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDelsRequest {
    List<Long> boardIds;
}
