package com.library.bookarte.borrow.repository;

import com.library.bookarte.borrow.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
}
