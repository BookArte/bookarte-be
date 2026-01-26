package com.library.bookarte.recommendation.entity;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.library.bookarte.global.base.BaseEntity;
import com.library.bookarte.recommendation.dto.RecommendationBookResDto;
import com.library.bookarte.recommendation.entity.type.RecommendType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.stream.Collectors;

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
    @Column
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

    public RecommendationBookResDto toResDto(){
        String authors = this.book.getParticipants().stream()
                .filter(p -> p.getType() == ParticipantType.AUTHOR)
                .map(Book.Participant::getName)
                .collect(Collectors.joining(", "));
        String translators = this.book.getParticipants().stream()
                .filter(p -> p.getType() == ParticipantType.TRANSLATOR)
                .map(Book.Participant::getName)
                .collect(Collectors.joining(", "));

        return RecommendationBookResDto.builder()
                .recommendationId(this.recommendationId)
                .recommendType(this.recommendType.getKey())
                .comments(this.comments)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .priority(this.priority)
                .bookId(this.book.getBookId())
                .bookTitle(this.book.getBookTitle())
                .bookAuthor(authors)
                .bookTranslator(translators)
                .publisherName(this.book.getPublisherName())
                .publicationDate(this.book.getPublicationDate())
                .bookIsbn(this.book.getBookIsbn())
                .bookContents(this.book.getBookContents())
                .bookThumbnail(this.book.getBookThumbnail())
                .bookCallNumber(this.book.getBookCallNumber())
                .bookCategoryName(this.book.getCategory().getCategoryName())
                .build();
    }


    public void updateRecommend(String comments,
                               LocalDate startDate,
                               LocalDate endDate){
        this.comments = comments;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
