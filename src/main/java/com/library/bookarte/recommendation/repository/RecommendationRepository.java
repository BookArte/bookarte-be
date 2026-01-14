package com.library.bookarte.recommendation.repository;

import com.library.bookarte.recommendation.entity.Recommendation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    List<Recommendation> findAllByOrderByPriorityAsc();
}