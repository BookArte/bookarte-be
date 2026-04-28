package com.library.bookarte.member.repository;

import com.library.bookarte.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.library.bookarte.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMembersByCursor(Long lastMemberId, String userId, int pageSize) {
        return queryFactory
                .selectFrom(member)
                .where(
                        userIdLt(userId),        // 아이디 검색 조건
                        cursorLt(lastMemberId)   // 커서 조건: 현재 ID보다 작은 데이터(내림차순 기준)
                )
                .orderBy(member.memberId.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression userIdLt(String userId) {
        return StringUtils.hasText(userId) ? member.memberUserId.contains(userId) : null;
    }

    private BooleanExpression cursorLt(Long lastMemberId) {
        if (lastMemberId == null) return null;
        return member.memberId.lt(lastMemberId);
    }
}
