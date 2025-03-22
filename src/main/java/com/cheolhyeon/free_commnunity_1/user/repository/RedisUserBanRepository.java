package com.cheolhyeon.free_commnunity_1.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisUserBanRepository {
    private final StringRedisTemplate reportRedisTemplate;

    private static final String KEY = "users:%s:ban";

    public boolean ban(Long userId, Duration ttl) {
        String key = generateKey(userId);
        return Boolean.TRUE.equals(reportRedisTemplate.opsForValue().setIfAbsent(key, "ban", ttl));
    }

    public boolean isUserBanned(Long userId) {
        String key = generateKey(userId);
        return Boolean.TRUE.equals(reportRedisTemplate.hasKey(key));
    }

    private String generateKey(Long userId) {
        return KEY.formatted(userId);
    }
}
