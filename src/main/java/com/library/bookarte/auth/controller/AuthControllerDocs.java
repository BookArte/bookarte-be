package com.library.bookarte.auth.controller;

import com.library.bookarte.auth.dto.request.LoginRequest;
import com.library.bookarte.auth.dto.request.MemberFindPasswordRequest;
import com.library.bookarte.auth.dto.request.VerifyCodeRequest;
import com.library.bookarte.auth.dto.response.MemberFindPasswordResponse;
import com.library.bookarte.auth.dto.response.TokenResponse;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth")
public interface AuthControllerDocs {

    /* Read: 로그인 */
    @Operation(summary = "로그인", description = "**성공 응답 데이터:** 로그인 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/login")
    ResponseEntity<GlobalResponseDto<TokenResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    );

    /* Read: 리프레시 토큰 생성 */
    @Operation(summary = "리프레시 토큰 생성", description = "**성공 응답 데이터:** 리프레시 토큰 생성 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리프레시 토큰 생성 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "리프레시 토큰이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/refresh")
    ResponseEntity<GlobalResponseDto<TokenResponse>> refresh(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response
    );

    /* Read: 로그아웃 */
    @Operation(summary = "로그아웃", description = "**성공 응답 데이터:** 로그아웃 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/logout")
    ResponseEntity<GlobalResponseDto<Void>> logout(
            @AuthenticationPrincipal Long memberId, // 현재 로그인한 사용자 ID
            HttpServletResponse response);

    /* Read: 비밀번호 찾기 */
    @Operation(summary = "비밀번호 찾기", description = "**성공 응답 데이터:** 비밀번호 찾기 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 찾기 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/find_password")
    ResponseEntity<GlobalResponseDto<MemberFindPasswordResponse>> findPassword(@RequestBody MemberFindPasswordRequest memberFindPasswordRequest);

    /* Read: 인증 코드 확인 */
    @Operation(summary = "인증 코드 확인", description = "**성공 응답 데이터:** 인증 코드 확인 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 코드 또는 만료된 코드"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/verify_code")
    ResponseEntity<GlobalResponseDto<Void>> verifyCode(@RequestBody VerifyCodeRequest verifyCodeRequest);
}
