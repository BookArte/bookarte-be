package com.library.bookarte.recommendation.repository;

import com.library.bookarte.recommendation.entity.Recommendation;
import com.library.bookarte.recommendation.entity.type.RecommendType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {


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

    boolean existsByBook_BookIdAndEndDateAfter(Long bookId, LocalDate now);

    //특정 기간 내 최대 우선순위 조회 (값이 없으면 0 반환)
    @Query("SELECT COALESCE(MAX(r.priority), 0) FROM Recommendation r " +
            "WHERE r.recommendType = :type " +
            "AND r.startDate <= :newEnd " +
            "AND r.endDate >= :newStart")
    int findMaxPriorityInPeriod(
            @Param("type") RecommendType type,
            @Param("newStart") LocalDate newStart,
            @Param("newEnd") LocalDate newEnd
    );

    //진행 중 및 예약 도서 전체 조회
    @Query("SELECT r FROM Recommendation r JOIN FETCH r.book " +
            "WHERE r.endDate >= :today " +
            "ORDER BY r.startDate ASC, r.priority ASC")
    List<Recommendation> findActiveAndUpcoming(@Param("today") LocalDate today);

    @Query("SELECT r FROM Recommendation r " +
            "WHERE r.recommendType = :type " +
            "AND r.startDate <= :endDate " +
            "AND r.endDate >= :startDate")
    List<Recommendation> findAllOverlapping(
            @Param("type") RecommendType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    //업데이트 대상 한권 제외 조회
    @Query("SELECT r FROM Recommendation r " +
            "WHERE r.recommendType = :type " +
            "AND r.startDate <= :newEnd " +
            "AND r.endDate >= :newStart " +
            "AND r.recommendationId != :excludeId")
    List<Recommendation> findOverlappingExceptSelf(
            @Param("type") RecommendType type,
            @Param("newStart") LocalDate newStart,
            @Param("newEnd") LocalDate newEnd,
            @Param("excludeId") Long excludeId
    );

    @Query("SELECT r FROM Recommendation r " +
            "WHERE r.endDate < :today ")
    Page<Recommendation> findHistory(
            @Param("today") LocalDate today,
            Pageable pageable
    );
}