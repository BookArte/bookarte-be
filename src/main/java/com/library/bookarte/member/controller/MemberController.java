package com.library.bookarte.member.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.member.dto.request.MemberDeleteRequest;
import com.library.bookarte.member.dto.request.MemberJoinRequest;
import com.library.bookarte.member.dto.request.MemberUpdateRequest;
import com.library.bookarte.member.dto.response.IdCheckResponse;
import com.library.bookarte.member.dto.response.MemberJoinResponse;
import com.library.bookarte.member.dto.response.MemberResponse;
import com.library.bookarte.member.dto.response.MemberUpdateResponse;
import com.library.bookarte.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<GlobalResponseDto<MemberJoinResponse>> join(@RequestBody MemberJoinRequest memberJoinRequest) {
        MemberJoinResponse result = memberService.join(memberJoinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponseDto.success(HttpStatus.CREATED, result));
    }

    @PatchMapping("/me")
    public ResponseEntity<GlobalResponseDto<MemberUpdateResponse>> updateMember(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MemberUpdateRequest memberUpdateRequest
    ) {
        MemberUpdateResponse result = memberService.updateMember(memberId, memberUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @GetMapping("/my")
    public ResponseEntity<GlobalResponseDto<MemberResponse>> getMember(@AuthenticationPrincipal Long memberId) {
        MemberResponse result = memberService.getMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @GetMapping("/id_check")
    public ResponseEntity<GlobalResponseDto<IdCheckResponse>> idCheck(@RequestParam("userId") String userId) {
        IdCheckResponse result = memberService.idCheck(userId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @PostMapping("/social")
    public ResponseEntity<GlobalResponseDto> joinSocial() {
        return null;
    }

    @PatchMapping("/delete")
    public ResponseEntity<GlobalResponseDto> deleteMember(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MemberDeleteRequest memberDeleteRequest
    ) {
        memberService.deleteMember(memberId, memberDeleteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, null));
    }

    @GetMapping("/point")
    public ResponseEntity<GlobalResponseDto> getPoint() {
        return null;
    }
}
