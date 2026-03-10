package com.library.bookarte.wish.repository;

import com.library.bookarte.wish.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Page<Wish> findByMember_MemberId(Long memberId, Pageable pageable);
}
