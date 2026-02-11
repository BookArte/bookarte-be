package com.library.bookarte.member.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.member.dto.request.*;
import com.library.bookarte.member.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member")
public interface MemberControllerDocs {

    /* Create: 회원 등록 */
    @Operation(summary = "회원 등록 요청", description = "**성공 응답 데이터:** 회원 등록 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    ResponseEntity<GlobalResponseDto<MemberJoinResponse>> join(@RequestBody MemberJoinRequest memberJoinRequest);

    /* Update: 회원 정보 수정 */
    @Operation(summary = "회원 정보 수정", description = "**성공 응답 데이터:** 회원 정보 수정 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/me")
    ResponseEntity<GlobalResponseDto<MemberUpdateResponse>> updateMember(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MemberUpdateRequest memberUpdateRequest
    );

    /* Read: 회원 정보 조회 */
    @Operation(summary = "회원 정보 조회", description = "**성공 응답 데이터:** 회원 정보 조회 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/my")
    ResponseEntity<GlobalResponseDto<MemberResponse>> getMember(@AuthenticationPrincipal Long memberId);

    /* Read: 아이디 중복체크 */
    @Operation(summary = "아이디 중복체크", description = "**성공 응답 데이터:** 아이디 중복체크 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 중복체크 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/id_check")
    ResponseEntity<GlobalResponseDto<IdCheckResponse>> idCheck(@RequestParam("userId") String userId);

    /* Update: 회원 정보 탈퇴 */
    @Operation(summary = "회원 정보 탈퇴", description = "**성공 응답 데이터:** 회원 정보 탈퇴 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/delete")
    ResponseEntity<GlobalResponseDto<Void>> deleteMember(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MemberDeleteRequest memberDeleteRequest
    );

    /* Read: 아이디 찾기 */
    @Operation(summary = "아이디 찾기", description = "**성공 응답 데이터:** 아이디 찾기 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 찾기 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/find_id")
    ResponseEntity<GlobalResponseDto<MemberFindIdResponse>> findId(@RequestBody MemberFindIdRequest memberFindIdRequest);

    /* Update: 비밀번호 수정 */
    @Operation(summary = "비밀번호 수정", description = "**성공 응답 데이터:** 비밀번호 수정 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/change_password")
    ResponseEntity<GlobalResponseDto<Void>> changePassword(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MemberChangePasswordRequest memberChangePasswordRequest
    );
}
