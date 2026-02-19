package com.library.bookarte.member.repository;

import com.library.bookarte.member.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberUserId(String userId);

    boolean existsByMemberUserId(String userId);

    Optional<List<Member>> findByMemberNameAndMemberTelAndMemberEmail(String memberName, String memberTel, String memberEmail);

    Optional<Member> findByMemberUserIdAndMemberNameAndMemberEmail(String memberUserId, String memberName, String memberEmail);

    @Query("SELECT m.memberUserId FROM Member m WHERE m.memberId = :memberId")
    Optional<String> findMemberUserIdByMemberId(@Param("memberId") Long memberId);
}
