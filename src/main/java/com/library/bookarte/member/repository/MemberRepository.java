package com.library.bookarte.member.repository;

import com.library.bookarte.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberUserId(String userId);

    boolean existsByMemberUserId(String userId);
}
