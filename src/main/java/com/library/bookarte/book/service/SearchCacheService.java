package com.library.bookarte.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SEARCH_RANK_KEY = "search:frequency";
    private static final String COUNT_CACHE_PREFIX = "book:count:";

    public Long getCachedTotalCount(String filterHash, int threshold, Supplier<Long> dbCountSupplier){
        Double score = redisTemplate.opsForZSet().incrementScore(SEARCH_RANK_KEY, filterHash, 1);

        if (score != null && score >= threshold) {
            String cacheKey = COUNT_CACHE_PREFIX + filterHash;
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

            if (cachedValue != null) {
                return Long.valueOf(cachedValue.toString());
            }

            //캐시에 없으면 DB 조회 후 저장 (TTL 30분)
            Long totalCount = dbCountSupplier.get();
            redisTemplate.opsForValue().set(cacheKey, totalCount, 30, TimeUnit.MINUTES);
            return totalCount;
        }

        return dbCountSupplier.get();
    }

    /**
     * 도서 데이터 변동(등록, 삭제, 복구 등) 시 Redis에 저장된 도서 카운트 캐시 일괄 삭제
     */
    public void clearCountCache() {
        try {
            Set<String> keys = redisTemplate.keys(COUNT_CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("도서 카운트 캐시 {}건 일괄 삭제 완료", keys.size());
            }
        } catch (Exception e) {
            log.warn("도서 카운트 캐시 일괄 삭제 중 오류 발생: {}", e.getMessage());
        }
    }
}
