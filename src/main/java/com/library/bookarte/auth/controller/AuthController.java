package com.library.bookarte.auth.controller;

import com.library.bookarte.auth.dto.request.LoginRequest;
import com.library.bookarte.auth.dto.request.RefreshTokenRequest;
import com.library.bookarte.auth.dto.response.TokenResponse;
import com.library.bookarte.auth.service.AuthService;
import com.library.bookarte.global.response.GlobalResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GlobalResponseDto<TokenResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = authService.login(loginRequest);

        ResponseCookie cookie = authService.createHttpOnlyCookie(tokenResponse.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, tokenResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<GlobalResponseDto<TokenResponse>> refresh(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = authService.refresh(refreshToken);

        ResponseCookie cookie = authService.createHttpOnlyCookie(tokenResponse.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalResponseDto<Void>> logout(
            @AuthenticationPrincipal Long memberId, // 현재 로그인한 사용자 ID
            HttpServletResponse response) {

        authService.logout(memberId);

        ResponseCookie cookie = authService.createLogoutCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(GlobalResponseDto.success(HttpStatus.OK, null));
    }
}
