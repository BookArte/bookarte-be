package com.library.bookarte.book.service;

import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.dto.response.BookResDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

@SpringBootTest
@ActiveProfiles("test")
public class UpgradeBookServiceTest {

    @Autowired private BookService bookService;

    @Test
    @DisplayName("고도화 LIKE 검색 성능 측정 - 제목 키워드")
    void upgradeLikeSearchTest() {
        SearchFilterDto filterDto = new SearchFilterDto();
        filterDto.setBookTitle("자바 개정");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("UPGRADE Title LIKE Search");

        Page<BookResDto> result = bookService.findBooksWithFilterAndFTS(filterDto, PageRequest.of(0, 10));

        stopWatch.stop();

        printResult(stopWatch, result);
    }


    private void printResult(StopWatch sw, Page<?> result) {
        System.out.println("\n-----------------------------------------");
        System.out.println("테스트명: " + sw.getLastTaskName());
        System.out.println("조회된 총 데이터 수: " + result.getTotalElements());
        System.out.println("현재 페이지 데이터 수: " + result.getContent().size());
        System.out.println("소요 시간: " + sw.getLastTaskTimeMillis() + " ms");
        System.out.println("-----------------------------------------\n");
    }

}
