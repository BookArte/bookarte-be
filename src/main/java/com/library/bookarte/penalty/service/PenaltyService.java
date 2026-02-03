package com.library.bookarte.penalty.service;

import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.penalty.entity.Penalty;
import com.library.bookarte.penalty.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;

    public void createPenalty(Member member, Borrow borrow, int overdueDays) {
        if (overdueDays <= 0) return;

        LocalDate today = LocalDate.now();

        LocalDate startDate = getLastestPenaltyEndDate(member.getMemberId(), today);
        LocalDate endDate = startDate.plusDays(overdueDays);

        String reason = "도서 [" + borrow.getBook().getBookTitle() + "] " + overdueDays + "일 연체";
        log.info(reason);

        Penalty penalty = Penalty.builder()
                .member(member)
                .borrow(borrow)
                .startDate(startDate)
                .endDate(endDate)
                .penaltyReason(reason)
                .isReleased(false)
                .build();




        penaltyRepository.save(penalty);
    }

    public LocalDate getLastestPenaltyEndDate(Long memberId, LocalDate today) {
        return penaltyRepository.findTopByMember_MemberIdOrderByEndDateDesc(memberId)
                .map(p -> p.getEndDate().isAfter(today) ? p.getEndDate() : today)
                .orElse(today);
    }
}
