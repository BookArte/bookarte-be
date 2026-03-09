package com.library.bookarte.board.repository;

import com.library.bookarte.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b WHERE TYPE(b) = :clazz")
    Page<Board> findAllByType(@Param("clazz") Class<? extends Board> clazz, Pageable pageable);
}
