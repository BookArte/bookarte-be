package com.library.bookarte.member.entity;

import com.library.bookarte.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 50, unique = true)
    private String memberUserId;

    @Column(nullable = false, length = 50)
    private String memberName;

    @Column(nullable = false, length = 255)
    private String memberTel;

    @Column(nullable = true, length = 255)
    private String memberPwd;

    @Column(nullable = false, length = 255)
    private String memberEmail;

    @Column(nullable = false, length = 15)
    private String memberSocialType;

    @Column(nullable = false, length = 15)
    private String memberRole;

    @Column(nullable = true, length = 255)
    private String memberSocialKey;

    @Column(nullable = true)
    private LocalDateTime memberLastLogin;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String useServiceYn;

    @Column(nullable = true)
    private LocalDateTime useServiceDate;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String usePrivacyYn;

    @Column(nullable = true)
    private LocalDateTime usePrivacyDate;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String memberMemo;

    @Column(nullable = false, length = 15)
    private String memberStatus;

    @Column(nullable = true)
    private LocalDateTime memberOutDate;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String memberOutReason;

    public void modify(String name, String tel, String email, String encodedPassword) {

        if (name != null) {
            this.memberName = name;
        }

        if (tel != null) {
            this.memberTel = tel;
        }

        if (email != null) {
            this.memberEmail = email;
        }

        if (encodedPassword != null) {
            this.memberPwd = encodedPassword;
        }

    }

    public void delete(String reason) {
        if (reason != null) {
            this.memberOutReason = reason;
        }
        this.memberOutDate = LocalDateTime.now();
        this.memberStatus = "STATUS02";
        this.memberPwd = null;
    }
}
