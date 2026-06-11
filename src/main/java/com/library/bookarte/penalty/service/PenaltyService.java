package com.library.bookarte.penalty.service;

import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import com.library.bookarte.penalty.dto.PenaltyResDto;
import com.library.bookarte.penalty.dto.ReleaseReqDto;
import com.library.bookarte.penalty.entity.Penalty;
import com.library.bookarte.penalty.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final MemberRepository memberRepository;

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
        return penaltyRepository.findTopByMember_MemberIdAndIsReleasedFalseOrderByEndDateDesc(memberId)
                .map(p -> p.getEndDate().isAfter(today) ? p.getEndDate() : today)
                .orElse(today);
    }

    //관리자 권한 도서 연체 패널티 해제
    public Long releasePenalty(Long penaltyId, Long memberId, ReleaseReqDto releaseReqDto) {
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PENALTY_NOT_FOUND));

        if (penalty.isReleased()) {
            throw new CustomException(CustomErrorCode.ALREADY_RELEASE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        long releasedDays = ChronoUnit.DAYS.between(
                penalty.getStartDate(),
                penalty.getEndDate()
        );

        penalty.releasePenalty(
                true,
                releaseReqDto.getReleaseReason(),
                member.getMemberUserId(),
                LocalDateTime.now()
        );

        shiftFuturePenalties(penalty, releasedDays);

        return penaltyId;
    }
    //연체 패널티 해제 철회
    public Long revokePenalty(Long penaltyId){
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PENALTY_NOT_FOUND));

        if(!penalty.isReleased()){
            throw new CustomException(CustomErrorCode.NOT_RELEASE);
        }
        Long memberId = Long.parseLong(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        penalty.cancelRelease(member.getMemberUserId());
        return penaltyId;
    }

    //연체 패널티 취소 사유 변경
    public Long updateReason(Long penaltyId, ReleaseReqDto releaseReqDto){
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PENALTY_NOT_FOUND));

        Long memberId = Long.parseLong(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        String modifiedBy = member.getMemberUserId();
        String releaseReason = releaseReqDto.getReleaseReason();
        penalty.updateReason(releaseReason,modifiedBy);

        return penaltyId;
    }

    //특정 유저 패널티 목록 조회
    public List<PenaltyResDto> getPenaltyList(String memberUserId){
        List<Penalty> penaltys = penaltyRepository.findByMember_MemberUserId(memberUserId);

        return penaltys.stream()
                .map(Penalty::toResDto)
                .toList();
    }

    //로그인한 유저 본인 패널티 확인
    public List<PenaltyResDto> getMyPenaltyList(Long memberId){
        List<Penalty> penalties = penaltyRepository.findByMember_MemberIdAndIsReleasedFalse(memberId);

        return penalties.stream()
                .map(Penalty::toResDto)
                .toList();
    }


    private void shiftFuturePenalties(Penalty releasedPenalty, long releasedDays) {
        if (releasedDays <= 0) return;

        List<Penalty> futurePenalties =
                penaltyRepository.findByMember_MemberIdAndIsReleasedFalseAndStartDateGreaterThanEqualOrderByStartDateAsc(
                        releasedPenalty.getMember().getMemberId(),
                        releasedPenalty.getEndDate()
                );

        for (Penalty penalty : futurePenalties) {
            penalty.updatePeriod(
                    penalty.getStartDate().minusDays(releasedDays),
                    penalty.getEndDate().minusDays(releasedDays)
            );
        }
    }
}
