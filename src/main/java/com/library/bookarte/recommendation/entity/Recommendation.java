package com.library.bookarte.recommendation.entity;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.global.base.BaseEntity;
import com.library.bookarte.recommendation.entity.type.RecommendType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="recommendation")
public class Recommendation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendationId;

    //추천 타입: 메인, 관리자 추천, 월간 추천, 주간 추천
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendType recommendType;

    //우선 순위
    @Column(nullable = false)
    private int priority;

    //추천 코멘트
    @Column(length = 255)
    private String comments;

    //추천 시작일
    @Column(nullable = false)
    private LocalDate startDate;

    //추천 종료일
    @Column(nullable = false)
    private LocalDate endDate;

    @OneToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
