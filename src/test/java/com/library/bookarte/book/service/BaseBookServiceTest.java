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

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
public class BaseBookServiceTest {

    @Autowired private BookService bookService;

    @Test
    @DisplayName("기존 LIKE 검색 성능 측정 - 제목 키워드")
    void baseLikeSearchTest() {
        SearchFilterDto filterDto = new SearchFilterDto();
        filterDto.setBookTitle("테스트");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Title LIKE Search");

        Page<BookResDto> result = bookService.findBooksWithFilter(filterDto, PageRequest.of(0, 10));

        stopWatch.stop();

        printResult(stopWatch, result);
    }

    @Test
    @DisplayName("기존 방식 성능 측정 - 복합 조건 검색 (카테고리 + 기간)")
    void baselineCompositeSearch() {
        SearchFilterDto filter = new SearchFilterDto();
        filter.setCategory("총류");
        filter.setPublicationDateStart(LocalDate.now().minusYears(5));
        filter.setPublicationDateEnd(LocalDate.now());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Composite Condition Search");

        Page<BookResDto> result = bookService.findBooksWithFilter(filter, PageRequest.of(0, 10));

        stopWatch.stop();

        printResult(stopWatch, result);
    }

    @Test
    @DisplayName("기존 방식 성능 측정 - 딥 페이징 (1000페이지)")
    void baselineDeepPagingSearch() {
        SearchFilterDto filter = new SearchFilterDto(); // 조건 없이 전체 조회

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Deep Paging (Page 1000)");

        // 1000번째 페이지 조회
        Page<BookResDto> result = bookService.findBooksWithFilter(filter, PageRequest.of(1000, 10));

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
