package com.library.bookarte.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
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


}
