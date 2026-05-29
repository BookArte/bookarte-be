package com.library.bookarte.member.entity.type;

import com.library.bookarte.board.entity.type.BoardType;

public enum MemberType {
    ROLE_ADMIN(MemberType.Constants.ROLE_ADMIN),    // 관리자
    ROLE_USER(MemberType.Constants.ROLE_USER),      // 회원

    SOCIAL_NONE(MemberType.Constants.SOCIAL_NONE),      // 없음
    SOCIAL_GOOGLE(MemberType.Constants.SOCIAL_GOOGLE),  // 구글
    SOCIAL_NAVER(MemberType.Constants.SOCIAL_NAVER),    // 네이버
    SOCIAL_KAKAO(MemberType.Constants.SOCIAL_KAKAO),    // 카카오

    STATUS_ACTIVE(MemberType.Constants.STATUS_ACTIVE),          // 활성
    STATUS_WITHDRAWN(MemberType.Constants.STATUS_WITHDRAWN);    // 탈퇴

    private final String value;

    MemberType(String value) {
        this.value = value;
    }

    public static class Constants {
        public static final String ROLE_ADMIN = "ROLE01";
        public static final String ROLE_USER = "ROLE02";

        public static final String SOCIAL_NONE = "SOCIAL01";
        public static final String SOCIAL_GOOGLE = "SOCIAL02";
        public static final String SOCIAL_NAVER = "SOCIAL03";
        public static final String SOCIAL_KAKAO = "SOCIAL04";

        public static final String STATUS_ACTIVE = "STATUS01";
        public static final String STATUS_WITHDRAWN = "STATUS02";
    }
}
