package com.cheolhyeon.free_commnunity_1.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ReportRedisRepository {
    private StringRedisTemplate reportRedisTemplate;
    private static final String KEY = "report:users:%s";

    public Long report(Long userId, Duration ttl) {
        String key = generateKey(userId);
        if (Boolean.TRUE.equals(reportRedisTemplate.hasKey(key))) {
            return reportRedisTemplate.opsForValue().increment(key);
        }
        reportRedisTemplate.opsForValue().set(key, "0", ttl);
        return reportRedisTemplate.opsForValue().increment(key);
    }

    private String generateKey(Long userId) {
        return KEY.formatted(userId);
    }
}
