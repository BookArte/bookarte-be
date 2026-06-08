package com.library.bookarte.auth.jwt;

import com.library.bookarte.auth.dto.response.TokenResponse;
import com.library.bookarte.member.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /* Access Token */
    public String createAccessToken(Member member) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(member.getMemberId()))
                .claim("userId", member.getMemberUserId())
                .claim("userName", member.getMemberName())
                .claim("role", member.getMemberRole())
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* Refresh Token */
    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getMemberId(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(getKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return "access".equals(claims.get("type"));
    }

}
