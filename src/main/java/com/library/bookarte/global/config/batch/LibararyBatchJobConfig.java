package com.library.bookarte.global.config.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LibararyBatchJobConfig {
    private final JobRepository jobRepository;

    /**
     * 전체 도서관 관리 마스터 배치
     */
    @Bean
    public Job libraryMasterJob(
            @Qualifier("overdueCheckStep") Step overdueCheckStep,
            @Qualifier("popularBookStatStep") Step popularBookStatStep,
            @Qualifier("bookMonthlyStatStep") Step bookMonthlyStatStep
    ) {
        return new JobBuilder("libraryMasterJob", jobRepository)
                .start(overdueCheckStep)      // 1. 연체 처리
                .next(popularBookStatStep)    // 2. 인기 도서 집계
                .next(bookMonthlyStatStep)    // 3. 도서별 1년간 월별 대출 통계 집계
                .build();
    }
}
