package com.library.bookarte.recommendation.service;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.service.BookService;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.recommendation.dto.RecommendationBookResDto;
import com.library.bookarte.recommendation.dto.RecommendationReqDto;
import com.library.bookarte.recommendation.dto.ReorderReqDto;
import com.library.bookarte.recommendation.dto.UpdateRecommendDto;
import com.library.bookarte.recommendation.entity.Recommendation;
import com.library.bookarte.recommendation.entity.type.RecommendType;
import com.library.bookarte.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final BookService bookService;

    private static final int MAX_RECOMMEND_COUNT = 10;

    //추천 도서 등록
    public void setRecommendBookByAdmin(RecommendationReqDto recommendationReqDto) {
        LocalDate newStartDate = recommendationReqDto.getStartDate();
        LocalDate newEndDate = recommendationReqDto.getEndDate();
        RecommendType type = RecommendType.ADMIN_PICK;

        if (newStartDate.isAfter(newEndDate)) {
            throw new CustomException(CustomErrorCode.INVALID_DATE_RANGE);
        }

        validateDailyRecommendationLimit(type,newStartDate,newEndDate,null);

        Book recommendationBook = bookService.findBook(recommendationReqDto.getBookId());

        int nextPriority = recommendationRepository.findMaxPriorityInPeriod(
                RecommendType.ADMIN_PICK,
                newStartDate,
                newEndDate
        ) + 1;

        Recommendation recommendation = Recommendation.builder()
                .book(recommendationBook)
                .recommendType(RecommendType.ADMIN_PICK)
                .priority(nextPriority)
                .comments(recommendationReqDto.getComments())
                .startDate(recommendationReqDto.getStartDate())
                .endDate(recommendationReqDto.getEndDate())
                .build();

        recommendationRepository.save(recommendation);
    }

    //추천 도서 삭제
    public void deleteRecommendBook(Long recommendationId){
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.RECOMMENDATION_NOT_FOUND));

        int deletedPriority = recommendation.getPriority();

        recommendationRepository.delete(recommendation);

        recommendationRepository.decreasePrioritiesHigherThan(deletedPriority);
    }

    //추천 도서 목록 조회
    @Transactional(readOnly = true)
    public List<RecommendationBookResDto> getRecommendationBooks() {
        LocalDate today = LocalDate.now();
        return recommendationRepository.findAllActiveRecommendations(today)
                .stream()
                .map(Recommendation::toResDto)
                .collect(Collectors.toList());
    }

    //추천 도서 상세 정보 조회
    public RecommendationBookResDto getRecommendationBookDetail(Long recommendationId){
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.RECOMMENDATION_NOT_FOUND));

        return recommendation.toResDto();
    }

    //추천 도서 목록 순서 변경
    public void reorderRecommendation(ReorderReqDto reorderReqDto) {
        List<Long> ids = reorderReqDto.getReorderedIds();

        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            int newPriority = i + 1;

            recommendationRepository.updatePriority(id, newPriority);
        }
    }

    //추천 정보 수정
    public void updateRecommend(Long recommendationId, UpdateRecommendDto updateRecommendDto){
        LocalDate updateStartDate = updateRecommendDto.getStartDate();
        LocalDate updateEndDate = updateRecommendDto.getEndDate();
        RecommendType type = RecommendType.ADMIN_PICK;

        if (updateStartDate.isAfter(updateEndDate)) {
            throw new CustomException(CustomErrorCode.INVALID_DATE_RANGE);
        }

        validateDailyRecommendationLimit(type,updateStartDate,updateEndDate,recommendationId);

        Recommendation target = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.RECOMMENDATION_NOT_FOUND));

        target.updateRecommend(updateRecommendDto.getComments(),
                updateRecommendDto.getStartDate(),
                updateRecommendDto.getEndDate());
    }

    //추천 도서 존재 유무
    public boolean existByBookId(Long bookId){
        LocalDate today = LocalDate.now();
        return recommendationRepository.existsByBook_BookIdAndEndDateAfter(bookId, today);
    }

    //추천 도서 현재 활성화 혹은 활성화 예정 이력 조회
    @Transactional(readOnly = true)
    public List<RecommendationBookResDto> findActiveRecommendations(){
        LocalDate today = LocalDate.now();
        List<Recommendation> recommendations = recommendationRepository.findActiveAndUpcoming(today);

        return recommendations.stream().map(Recommendation::toResDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<RecommendationBookResDto> findRecommendationsHistory(Pageable pageable){
        LocalDate today = LocalDate.now();
        Page<Recommendation> hitorys = recommendationRepository.findHistory(today, pageable);

        return hitorys.map(Recommendation::toResDto);
    }

    /**
     * 특정 기간 내의 각 날짜별 추천 도서 권수가 제한을 초과하는지 검증 함수.
     * @param type 추천 타입
     * @param start 시작일
     * @param end 종료일
     * @param excludeId 제외할 추천 ID (수정 시 본인 제외용, 등록 시 null)
     */

    private void validateDailyRecommendationLimit(RecommendType type, LocalDate start, LocalDate end, Long excludeId) {
        List<Recommendation> overlappingBooks = recommendationRepository.findAllOverlapping(type, start, end);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            final LocalDate checkDate = date;

            long dailyCount = overlappingBooks.stream()
                    .filter(r -> (excludeId == null || !r.getRecommendationId().equals(excludeId))) // 수정 중인 본인 제외
                    .filter(r -> !checkDate.isBefore(r.getStartDate()) && !checkDate.isAfter(r.getEndDate()))
                    .count();

            if (dailyCount >= MAX_RECOMMEND_COUNT) {
                throw new CustomException(CustomErrorCode.RECOMMENDATION_LIMIT_EXCEEDED);
            }
        }
    }




}
