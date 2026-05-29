package com.library.bookarte.global.config.batch;

import com.library.bookarte.borrow.dto.cache.PopularBookCacheDto;
import com.library.bookarte.borrow.service.BorrowCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PopularBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BorrowCacheService borrowCacheService;

    @Qualifier("objectRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public Job popularJob(Step popularBookStatStep) {
        return new JobBuilder("staticsJob", jobRepository)
                .start(popularBookStatStep)
                .build();
    }

    @Bean
    public Step popularBookStatStep() {
        return new StepBuilder("popularBookStatStep", jobRepository)
                .<String, Map.Entry<String, PopularBookCacheDto>> chunk(1)
                .transactionManager(transactionManager)
                .reader(periodReader())
                .processor(periodProcessor())
                .writer(cacheWarmingWriter())
                .build();
    }

    @Bean
    public ListItemReader<String> periodReader() {
        return new ListItemReader<>(List.of("WEEK", "MONTH", "YEAR"));
    }

    @Bean
    public ItemProcessor<String, Map.Entry<String, PopularBookCacheDto>> periodProcessor() {
        return period -> Map.entry(period, borrowCacheService.getTop100Cache(period));
    }

    @Bean
    public ItemWriter<Map.Entry<String, PopularBookCacheDto>> cacheWarmingWriter() {
        return chunk -> {
            for (var entry : chunk) {
                String cacheKey = "popularBooks::" + entry.getKey();
                redisTemplate.opsForValue().set(cacheKey, entry.getValue(), Duration.ofHours(24));
                log.info(">>>> Redis 캐시 강제 갱신 완료 (Key: {})", cacheKey);
            }
        };
    }
}
