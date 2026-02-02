package com.library.bookarte.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    /**
     * 사용자 아이디 마스킹 (앞 3자리 제외 후 나머지 *)
     */
    public static String maskUserId(String userId) {
        if (userId == null || userId.length() < 3) {
            return userId;
        }

        return userId.substring(0, 3) + "*".repeat(userId.length() - 3);
    }

    /**
     * 6자리 랜덤코드 생성
     */
    public static String generateRandomCode() {
        SecureRandom sr = new SecureRandom();
        return IntStream.range(0, 6)
                .map(i -> sr.nextInt(10))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}
