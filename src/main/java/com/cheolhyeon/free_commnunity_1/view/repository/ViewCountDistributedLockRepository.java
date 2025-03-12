package com.cheolhyeon.free_commnunity_1.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ViewCountDistributedLockRepository {
    private static final String KEY = "post:%s:user:%s:lock";
    private final StringRedisTemplate viewCountRedisTemplate;

    public boolean lock(Long postId, Long userId, Duration ttl) {
        String key = generateKey(postId, userId);
        return Boolean.TRUE.equals(viewCountRedisTemplate.opsForValue().setIfAbsent(key, "", ttl));
    }

    private String generateKey(Long postId, Long userId) {
        return KEY.formatted(postId, userId);
    }
}
