package com.library.bookarte.auth.service;

import com.library.bookarte.auth.dto.request.LoginRequest;
import com.library.bookarte.auth.dto.request.MemberFindPasswordRequest;
import com.library.bookarte.auth.dto.request.ResetPasswordRequest;
import com.library.bookarte.auth.dto.request.VerifyCodeRequest;
import com.library.bookarte.auth.dto.response.MemberFindPasswordResponse;
import com.library.bookarte.auth.dto.response.TokenResponse;
import com.library.bookarte.auth.dto.response.VerifyCodeResponse;
import com.library.bookarte.auth.jwt.JwtProvider;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.global.util.MailService;
import com.library.bookarte.global.util.StringUtils;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = CustomException.class)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final MailService mailService;

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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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
        Member member = memberRepository.findByMemberUserIdAndMemberNameAndMemberEmail(
                memberFindPasswordRequest.getMemberUserId(),
                memberFindPasswordRequest.getMemberName(),
                memberFindPasswordRequest.getMemberEmail()
        ).orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        String authCode = StringUtils.generateRandomCode();

        redisTemplate.opsForValue().set(
                "AUTH_CODE:" + member.getMemberId(),
                authCode,
                Duration.ofSeconds(authCodeExpiration)
        );

        mailService.sendAuthMail(memberFindPasswordRequest.getMemberEmail(), authCode);
        System.out.println("인증코드 발송 완료: " + authCode);

        return MemberFindPasswordResponse.builder()
                .memberId(member.getMemberId())
                .expiresIn(authCodeExpiration)
                .build();
    }

    public VerifyCodeResponse verifyCode(VerifyCodeRequest verifyCodeRequest) {
        String redisKey = "AUTH_CODE:" + verifyCodeRequest.getMemberId();
        String savedCode = redisTemplate.opsForValue().get(redisKey);

        if (savedCode == null) throw new CustomException(CustomErrorCode.AUTH_CODE_EXPIRED);
        if (!savedCode.equals(verifyCodeRequest.getCode()))
            throw new CustomException(CustomErrorCode.INVALID_AUTH_CODE);

        redisTemplate.delete(redisKey);

        String resetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "RESET_TOKEN:" + verifyCodeRequest.getMemberId(),
                resetToken,
                Duration.ofMinutes(5)
        );

        return VerifyCodeResponse.builder()
                .memberId(verifyCodeRequest.getMemberId())
                .resetToken(resetToken)
                .build();
    }

    public void resetPassword(ResetPasswordRequest request) {
        String redisKey = "RESET_TOKEN:" + request.getId();
        String savedToken = redisTemplate.opsForValue().get(redisKey);

        if (savedToken == null || !savedToken.equals(request.getResetToken())) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }

        Member member = memberRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(request.getMemberPassword());

        member.updatePassword(encodedPassword);

        redisTemplate.delete(redisKey);

    }
}
