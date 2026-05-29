package com.library.bookarte.borrow.repository;

import com.library.bookarte.borrow.dto.response.MonthlyData;
import com.library.bookarte.borrow.entity.stats.BookMonthlyStats;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookMonthlyStatsRepository extends JpaRepository<BookMonthlyStats, Long> {
    Optional<BookMonthlyStats> findByBookIdAndStatYearAndStatMonth(Long bookId, int statYear, int statMonth);

    @Query("""
    SELECT new com.library.bookarte.borrow.dto.response.MonthlyData(s.statYear, s.statMonth, s.borrowCount)
    FROM BookMonthlyStats s
    WHERE s.bookId = :bookId
      AND (s.statYear > :year OR (s.statYear = :year AND s.statMonth >= :month))
      AND (s.statYear < YEAR(CURRENT_DATE) OR (s.statYear = YEAR(CURRENT_DATE) AND s.statMonth < MONTH(CURRENT_DATE)))
    ORDER BY s.statYear ASC, s.statMonth ASC
""")
    List<MonthlyData> findLastYearStats(@Param("bookId") Long bookId,
                                        @Param("year") int year,
                                        @Param("month") int month);
}
