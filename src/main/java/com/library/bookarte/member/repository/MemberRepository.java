package com.library.bookarte.member.repository;

import com.library.bookarte.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberUserId(String userId);

    boolean existsByMemberUserId(String userId);

    Optional<List<Member>> findByMemberNameAndMemberTelAndMemberEmail(String memberName, String memberTel, String memberEmail);

    boolean existsByMemberUserIdAndMemberNameAndMemberEmail(String memberUserId, String memberName, String memberEmail);
}
