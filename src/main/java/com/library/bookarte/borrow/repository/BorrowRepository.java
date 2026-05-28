package com.library.bookarte.borrow.repository;

import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.borrow.entity.type.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long>, BorrowRepositoryCustom {
    List<Borrow> findAllByStatusAndReturnDueDateBefore(Status status, LocalDate today);
    boolean existsByMember_MemberIdAndStatus(Long memberId, Status status);
    long countBorrowByMember_MemberIdAndStatus(Long memberId, Status status);
    List<Borrow> findByMember_MemberId(Long memberId);
}
