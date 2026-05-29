package com.library.bookarte.recommendation.repository;

import java.util.List;

public interface RecommendationRepositoryCustom {
    void deleteRecommendationsByBookIds(List<Long> bookIds);
}
