package com.library.bookarte.wish.repository;

import com.library.bookarte.wish.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Page<Wish> findByMember_MemberIdAndBook_DeletedAtIsNull(Long memberId, Pageable pageable);
    void deleteByMember_MemberIdAndBook_BookId(Long memberId, Long bookId);
    void deleteByBook_BookIdIn(List<Long> bookIds);
    long countWishByMember_MemberId(Long memberId);
}
