package com.library.bookarte.auth.service;

import com.library.bookarte.auth.dto.request.LoginRequest;
import com.library.bookarte.auth.dto.request.MemberFindPasswordRequest;
import com.library.bookarte.auth.dto.request.VerifyCodeRequest;
import com.library.bookarte.auth.dto.response.MemberFindPasswordResponse;
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

import java.security.SecureRandom;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Value("${auth.code.expiration}")
    private long authCodeExpiration;

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

    public MemberFindPasswordResponse findPassword(MemberFindPasswordRequest memberFindPasswordRequest) {
        boolean exists = memberRepository.existsByMemberUserIdAndMemberNameAndMemberEmail(
                memberFindPasswordRequest.getMemberUserId(),
                memberFindPasswordRequest.getMemberName(),
                memberFindPasswordRequest.getMemberEmail()
        );

        if (!exists) {
            throw new CustomException(CustomErrorCode.MEMBER_NOT_FOUND);
        }

        String authCode = generateRandomCode();

        redisTemplate.opsForValue().set(
                "AUTH:" + memberFindPasswordRequest.getMemberEmail(),
                authCode,
                Duration.ofSeconds(authCodeExpiration)
        );

        System.out.println("인증코드 발송 완료: " + authCode);

        return MemberFindPasswordResponse.builder()
                .expiresIn(authCodeExpiration)
                .build();
    }

    private String generateRandomCode() {
        SecureRandom sr = new SecureRandom();
        return IntStream.range(0, 6)
                .map(i -> sr.nextInt(10))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public void verifyCode(VerifyCodeRequest verifyCodeRequest) {
        String savedCode = redisTemplate.opsForValue().get("AUTH:" + verifyCodeRequest.getMemberEmail());

        if (savedCode == null) {
            throw new CustomException(CustomErrorCode.AUTH_CODE_EXPIRED);
        }

        if (!savedCode.equals(verifyCodeRequest.getCode())) {
            throw new CustomException(CustomErrorCode.INVALID_AUTH_CODE);
        }

        redisTemplate.delete("AUTH:" + verifyCodeRequest.getMemberEmail());
    }
}
