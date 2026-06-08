package com.library.bookarte.member.repository;

import com.library.bookarte.member.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMembersByCursor(Long lastMemberId, String userId, int pageSize);
}
