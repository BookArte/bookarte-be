package com.library.bookarte.auth.service;

import com.library.bookarte.auth.dto.request.LoginRequest;
import com.library.bookarte.auth.dto.request.RefreshTokenRequest;
import com.library.bookarte.auth.dto.response.TokenResponse;
import com.library.bookarte.auth.jwt.JwtProvider;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.access-expiration}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    public TokenResponse login(LoginRequest loginRequest) {

        Member member = memberRepository.findByMemberUserId(loginRequest.getMemberUserId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getMemberPassword(), member.getMemberPwd())) {
            throw new CustomException(CustomErrorCode.INVALID_PASSWORD);
        }

        return generateTokenResponse(member);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validate(refreshToken)) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }

        Long memberId = jwtProvider.getMemberId(refreshToken);
        String savedToken = redisTemplate.opsForValue()
                .get(REFRESH_TOKEN_PREFIX + memberId);

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        return generateTokenResponse(member);
    }

    private TokenResponse generateTokenResponse(Member member) {
        String accessToken = jwtProvider.createAccessToken(member);
        String refreshToken = jwtProvider.createRefreshToken(member.getMemberId());

        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + member.getMemberId(),
                refreshToken,
                Duration.ofMillis(refreshExpirationMs)
        );

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessExpirationMs / 1000)
                .build();
    }

    public ResponseCookie createHttpOnlyCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshExpirationMs / 1000)
                .sameSite("Lax")
                .build();
    }

    public void logout(Long memberId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + memberId);
    }

    public ResponseCookie createLogoutCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}
