package com.library.bookarte.borrow.dto.cache;

import com.library.bookarte.borrow.dto.response.PopularBookResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PopularBookCacheDto implements Serializable {
    private List<PopularBookResDto> content;
    private long totalElements;
}
