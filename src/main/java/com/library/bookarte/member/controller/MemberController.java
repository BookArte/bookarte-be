package com.library.bookarte.member.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.member.dto.request.*;
import com.library.bookarte.member.dto.response.*;
import com.library.bookarte.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {
    private final MemberService memberService;

    @Override
    public ResponseEntity<GlobalResponseDto<MemberJoinResponse>> join(@RequestBody MemberJoinRequest memberJoinRequest) {
        MemberJoinResponse result = memberService.join(memberJoinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponseDto.success(HttpStatus.CREATED, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<MemberUpdateResponse>> updateMember(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MemberUpdateRequest memberUpdateRequest
    ) {
        MemberUpdateResponse result = memberService.updateMember(memberId, memberUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<MemberResponse>> getMember(@AuthenticationPrincipal Long memberId) {
        MemberResponse result = memberService.getMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<IdCheckResponse>> idCheck(@RequestParam("userId") String userId) {
        IdCheckResponse result = memberService.idCheck(userId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @PostMapping("/social")
    public ResponseEntity<GlobalResponseDto> joinSocial() {
        return null;
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Void>> deleteMember(
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

    @Override
    public ResponseEntity<GlobalResponseDto<MemberFindIdResponse>> findId(@RequestBody MemberFindIdRequest memberFindIdRequest) {
        MemberFindIdResponse result = memberService.findId(memberFindIdRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Void>> changePassword(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MemberChangePasswordRequest memberChangePasswordRequest
    ) {
        memberService.changePassword(memberId, memberChangePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, null));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<List<MemberResponse>>> getMemberList(@RequestParam(required = false) String userId){
        List<MemberResponse> result = memberService.findListByMemberUserID(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }
}
