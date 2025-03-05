package com.cheolhyeon.free_commnunity_1.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ViewCountRedisRepository {
    private final static String KEY = "post:%s:view_count";
    private final StringRedisTemplate redisTemplate;

    public Long read(Long postId) {
        String result = redisTemplate.opsForValue().get(generateKey(postId));
        return result == null ? 0L : Long.parseLong(result);
    }

    public Long increase(Long postId) {
        return redisTemplate.opsForValue().increment(generateKey(postId));
    }


    private String generateKey(Long postId) {
        return KEY.formatted(postId);
    }
}
