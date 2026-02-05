package com.library.bookarte.recommendation.repository;

import com.library.bookarte.recommendation.entity.Recommendation;
import com.library.bookarte.recommendation.entity.type.RecommendType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    //우선 순위 자동 증가
    @Modifying
    @Query("UPDATE Recommendation r SET r.priority = r.priority + 1 WHERE r.priority >= 1")
    void shiftPriorities();

    //우선 순위 자동 감소
    @Modifying
    @Query("UPDATE Recommendation r SET r.priority = r.priority - 1 WHERE r.priority > :deletedPriority")
    void decreasePrioritiesHigherThan(@Param("deletedPriority") int deletedPriority);

    // 현재 유효한 추천 도서 목록만 조회 (시작일 <= 오늘 <= 종료일)
    @Query("SELECT r FROM Recommendation r " +
            "WHERE r.startDate <= :today AND r.endDate >= :today " +
            "ORDER BY r.priority ASC")
    List<Recommendation> findAllActiveRecommendations(@Param("today") LocalDate today);

    @Modifying
    @Query("UPDATE Recommendation r SET r.priority = :priority WHERE r.id = :id")
    void updatePriority(@Param("id") Long id, @Param("priority") int priority);

    boolean existsByBook_BookId(Long bookId);

    int countByRecommendType(RecommendType recommendType);
}