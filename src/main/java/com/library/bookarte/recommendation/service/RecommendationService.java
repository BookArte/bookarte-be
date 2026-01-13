package com.library.bookarte.recommendation.service;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.service.BookService;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.recommendation.dto.RecommendationReqDto;
import com.library.bookarte.recommendation.entity.Recommendation;
import com.library.bookarte.recommendation.entity.type.RecommendType;
import com.library.bookarte.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final BookService bookService;

    public void setRecommendBookByAdmin(RecommendationReqDto recommendationReqDto) {
        Book recommendationBook = bookService.findBook(recommendationReqDto.getBookId());

        Recommendation recommendation = Recommendation.builder()
                .book(recommendationBook)
                .recommendType(RecommendType.ADMIN_PICK)
                .priority(recommendationReqDto.getPriority())
                .comments(recommendationReqDto.getComments())
                .startDate(recommendationReqDto.getStartDate())
                .endDate(recommendationReqDto.getEndDate())
                .build();

        recommendationRepository.save(recommendation);

    }

}
