package com.cheolhyeon.free_commnunity_1.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ViewCountDistributedLockRepository {
    private final static String KEY = "post:%s:user:%s:lock";
    private final StringRedisTemplate redisTemplate;

    public boolean lock(Long postId, Long userId, Duration ttl) {
        String key = generateKey(postId, userId);
        // 이미 존재하면 false 반환, 없다면 true 반환
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "", ttl));
    }

    private String generateKey(Long postId, Long userId) {
        return KEY.formatted(postId, userId);
    }
}
