package com.library.bookarte.penalty.repository;


import com.library.bookarte.penalty.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    // 가장 최근 패널티 종료일을 가진 패널티 객체 조회
    Optional<Penalty> findTopByMember_MemberIdOrderByEndDateDesc(Long memberId);


}
