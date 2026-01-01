package com.library.bookarte.auth.service;

import com.library.bookarte.auth.dto.request.LoginRequest;
import com.library.bookarte.auth.dto.response.TokenResponse;
import com.library.bookarte.auth.jwt.JwtProvider;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public TokenResponse login(LoginRequest loginRequest) {
        Member member = memberRepository.findByMemberUserId(loginRequest.getUserId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.DATA_INTEGRITY_VIOLATION)); // 에러코드 수정

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getMemberPwd())) {
            throw new CustomException(CustomErrorCode.DATA_INTEGRITY_VIOLATION);    // 에러코드 수정
        }

        return jwtProvider.createToken(member);
    }
}
