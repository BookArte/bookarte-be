package com.library.bookarte.member.service;

import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.global.util.StringUtils;
import com.library.bookarte.member.dto.request.MemberDeleteRequest;
import com.library.bookarte.member.dto.request.MemberFindIdRequest;
import com.library.bookarte.member.dto.request.MemberJoinRequest;
import com.library.bookarte.member.dto.request.MemberUpdateRequest;
import com.library.bookarte.member.dto.response.*;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = CustomException.class)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberJoinResponse join(MemberJoinRequest memberJoinRequest) {
        LocalDateTime now = LocalDateTime.now();
        Member member = Member.builder()
                .memberUserId(memberJoinRequest.getMemberUserId())
                .memberName(memberJoinRequest.getMemberName())
                .memberTel(memberJoinRequest.getMemberTel())
                .memberPwd(passwordEncoder.encode(memberJoinRequest.getMemberPassword()))
                .memberEmail(memberJoinRequest.getMemberEmail())
                .memberRole("ROLE01")               // 상수 ENUM 작업
                .memberSocialType("SOCIAL01")       // 상수 ENUM 작업
                .memberStatus("STATUS01")           // 상수 ENUM 작업
                .usePrivacyYn(memberJoinRequest.getAgreePrivacy() ? "Y" : "N")
                .useServiceYn(memberJoinRequest.getAgreeService() ? "Y" : "N")
                .usePrivacyDate(memberJoinRequest.getAgreePrivacy() ? now : null)
                .useServiceDate(memberJoinRequest.getAgreeService() ? now : null)
                .build();

        Member resultMember = memberRepository.save(member);

        return MemberJoinResponse.builder()
                .id(resultMember.getMemberId())
                .userId(resultMember.getMemberUserId())
                .name(resultMember.getMemberName())
                .email(resultMember.getMemberEmail())
                .build();
    }

    public MemberUpdateResponse updateMember(Long memberId, MemberUpdateRequest memberUpdateRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        String encodedPassword = null;
        if (memberUpdateRequest.getPassword() != null && !memberUpdateRequest.getPassword().isBlank()) {
            encodedPassword = passwordEncoder.encode(memberUpdateRequest.getPassword());
        }

        member.modify(
                memberUpdateRequest.getName(),
                memberUpdateRequest.getTel(),
                memberUpdateRequest.getEmail(),
                encodedPassword
        );
        return MemberUpdateResponse.builder()
                .name(memberUpdateRequest.getName())
                .tel(memberUpdateRequest.getTel())
                .email(memberUpdateRequest.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        return MemberResponse.builder()
                .id(member.getMemberId())
                .userId(member.getMemberUserId())
                .name(member.getMemberName())
                .email(member.getMemberEmail())
                .tel(member.getMemberTel())
                .build();
    }

    @Transactional(readOnly = true)
    public IdCheckResponse idCheck(String userId) {
        boolean exists = memberRepository.existsByMemberUserId(userId);

        return IdCheckResponse.builder()
                .userId(userId)
                .available(!exists)
                .build();
    }

    public void deleteMember(Long memberId, MemberDeleteRequest memberDeleteRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        if ("STATUS02".equals(member.getMemberStatus())) {
            throw new CustomException(CustomErrorCode.MEMBER_DELETE_STATUS_ERROR);
        }

        member.delete(memberDeleteRequest.getReason());
    }

    public MemberFindIdResponse findId(MemberFindIdRequest memberFindIdRequest) {
        List<Member> members = memberRepository.findByMemberNameAndMemberTelAndMemberEmail(
                memberFindIdRequest.getMemberName(),
                memberFindIdRequest.getMemberTel(),
                memberFindIdRequest.getMemberEmail()
        ).orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_USER_ID_NOT_FOUND));

        List<String> userIds = members.stream()
                .map(member -> StringUtils.maskUserId(member.getMemberUserId()))
                .toList();

        return MemberFindIdResponse.builder()
                .userIds(userIds)
                .build();
    }
}
