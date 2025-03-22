package com.cheolhyeon.free_commnunity_1.report.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ReportRedisRepository {
    private final StringRedisTemplate reportRedisTemplate;
    private static final String KEY = "report:users:%s";

    public Long report(Long userId, Duration ttl) {
        String key = generateKey(userId);
        reportRedisTemplate.opsForValue().setIfAbsent(key, "0", ttl);
        return reportRedisTemplate.opsForValue().increment(key);
    }


    public Long getReportCount(Long userId) {
        String value = reportRedisTemplate.opsForValue().get(generateKey(userId));
        return value == null ? 0L : Long.parseLong(value);
    }

    private String generateKey(Long userId) {
        return KEY.formatted(userId);
    }
}
