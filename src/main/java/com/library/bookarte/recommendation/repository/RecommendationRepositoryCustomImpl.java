package com.library.bookarte.recommendation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.library.bookarte.recommendation.entity.QRecommendation.recommendation;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RecommendationRepositoryCustomImpl implements RecommendationRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteRecommendationsByBookIds(List<Long> bookIds){
        jpaQueryFactory
                .delete(recommendation)
                .where(recommendation.book.bookId.in(bookIds))
                .execute();
    }

}
