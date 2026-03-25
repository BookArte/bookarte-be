package com.library.bookarte.board.repository;

import com.library.bookarte.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query(value = "SELECT b FROM Board b WHERE TYPE(b) = :clazz",
            countQuery = "SELECT count(b) FROM Board b WHERE TYPE(b) = :clazz")
    Page<Board> findAllByType(@Param("clazz") Class<? extends Board> clazz, Pageable pageable);

    @Query(value = "SELECT b FROM Board b WHERE TYPE(b) = :clazz " +
            "AND (:searchText IS NULL OR b.title LIKE %:searchText% OR b.contents LIKE %:searchText%) " +
            "AND (:startDate IS NULL OR b.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR b.createdAt <= :endDate)",
            countQuery = "SELECT count(b) FROM Board b WHERE TYPE(b) = :clazz " +
                    "AND (:searchText IS NULL OR b.title LIKE %:searchText% OR b.contents LIKE %:searchText%) " +
                    "AND (:startDate IS NULL OR b.createdAt >= :startDate) " +
                    "AND (:endDate IS NULL OR b.createdAt <= :endDate)")
    Page<Board> findAllByTypeAndSearch(
            @Param("clazz") Class<? extends Board> clazz,
            @Param("searchText") String searchText,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

}
