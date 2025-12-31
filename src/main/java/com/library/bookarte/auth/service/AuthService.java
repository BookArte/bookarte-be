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

        Member member = memberRepository.findByMemberUserId(loginRequest.getUserId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getMemberPwd())) {
            throw new CustomException(CustomErrorCode.INVALID_PASSWORD);
        }

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

    public TokenResponse refresh(RefreshTokenRequest refreshTokenRequest) {

        String refreshToken = refreshTokenRequest.getRefreshToken();

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

        String newAccessToken = jwtProvider.createAccessToken(member);
        String newRefreshToken = jwtProvider.createRefreshToken(memberId);

        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + memberId,
                newRefreshToken,
                Duration.ofMillis(refreshExpirationMs)
        );

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(accessExpirationMs / 1000)
                .build();
    }
}
