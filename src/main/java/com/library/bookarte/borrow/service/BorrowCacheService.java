package com.library.bookarte.borrow.service;

import com.library.bookarte.borrow.dto.cache.PopularBookCacheDto;
import com.library.bookarte.borrow.dto.response.PopularBookResDto;
import com.library.bookarte.borrow.repository.BorrowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowCacheService {
    private final BorrowRepository borrowRepository;

    @Cacheable(value = "popularBooks", key = "#period")
    public PopularBookCacheDto getTop100Cache(String period) {
        List<PopularBookResDto> top100 = borrowRepository.findTopPopularBooks(period, 100);
        // List를 직접 던지지 말고, 다시 DTO로 감싸서 반환하세요.
        return new PopularBookCacheDto(top100, (long) top100.size());
    }
}
