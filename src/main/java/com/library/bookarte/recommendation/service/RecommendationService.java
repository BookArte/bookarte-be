package com.library.bookarte.recommendation.service;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.service.BookService;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.recommendation.dto.RecommendationBookResDto;
import com.library.bookarte.recommendation.dto.RecommendationReqDto;
import com.library.bookarte.recommendation.entity.Recommendation;
import com.library.bookarte.recommendation.entity.type.RecommendType;
import com.library.bookarte.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final BookService bookService;

    public void setRecommendBookByAdmin(RecommendationReqDto recommendationReqDto) {
        Book recommendationBook = bookService.findBook(recommendationReqDto.getBookId());
        int defaultPriority = 1; //나중에 추천 리스트에 등록되는 도서는 1순위로 들어가게 된다

        //선행 등록되어있던 도서 우선순위를 한칸씩 미룸
        recommendationRepository.shiftPriorities();

        Recommendation recommendation = Recommendation.builder()
                .book(recommendationBook)
                .recommendType(RecommendType.ADMIN_PICK)
                .priority(defaultPriority)
                .comments(recommendationReqDto.getComments())
                .startDate(recommendationReqDto.getStartDate())
                .endDate(recommendationReqDto.getEndDate())
                .build();

        recommendationRepository.save(recommendation);

    }

    public void deleteRecommendBook(Long recommendationId){
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.RECOMMENDATION_NOT_FOUND));

        int deletedPriority = recommendation.getPriority();

        recommendationRepository.delete(recommendation);

        recommendationRepository.decreasePrioritiesHigherThan(deletedPriority);
    }

    public List<RecommendationBookResDto> getRecommendationBooks() {
        return recommendationRepository.findAllByOrderByPriorityAsc()
                .stream()
                .map(Recommendation::toResDto)
                .collect(Collectors.toList());
    }


}
