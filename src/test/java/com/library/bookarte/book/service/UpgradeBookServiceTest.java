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

    @Test
    @DisplayName("빈도 기반 캐싱 적용 후 성능 측정 - 인기 키워드 시나리오")
    void cachedSearchPerformanceTest() {
        SearchFilterDto filterDto = new SearchFilterDto();
        filterDto.setBookTitle("자바 개정");
        int threshold = 5; // 설정한 임계치
        PageRequest pageRequest = PageRequest.of(0, 10);

        StopWatch stopWatch = new StopWatch();

        // [Step 1] Warm-up: 임계치 직전까지 검색 수행 (캐싱 전)
        System.out.println("=== 1~4회차: 빈도 누적 단계 (DB 조회 예상) ===");
        for (int i = 1; i < threshold; i++) {
            bookService.findBooksWithFilterAndFTS(filterDto, pageRequest);
        }

        // [Step 2] Threshold 달성 회차 측정 (DB 조회 + 캐시 적재)
        stopWatch.start("Threshold Reach (5th Search - DB + Cache Writing)");
        Page<BookResDto> firstCacheResult = bookService.findBooksWithFilterAndFTS(filterDto, pageRequest);
        stopWatch.stop();

        // [Step 3] Cache Hit 측정 (완전한 캐시 데이터 반환)
        stopWatch.start("Cache Hit (6th Search - Redis Only)");
        Page<BookResDto> cachedResult = bookService.findBooksWithFilterAndFTS(filterDto, pageRequest);
        stopWatch.stop();

        // 결과 출력
        printResult(stopWatch, cachedResult);

        // 검증: 캐시 적중 시 소요 시간은 극단적으로 짧아야 함 (예: 10ms 내외)
        long cachedTime = stopWatch.getLastTaskTimeMillis();
        System.out.println("캐시 적중 시 응답 시간: " + cachedTime + "ms");
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
