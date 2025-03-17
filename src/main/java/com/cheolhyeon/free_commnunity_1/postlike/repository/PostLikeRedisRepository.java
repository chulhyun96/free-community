package com.cheolhyeon.free_commnunity_1.postlike.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostLikeRedisRepository {
    private static final String KEY = "post:%s:like_count";
    private final StringRedisTemplate postLikeRedisTemplate;

    public Long increment(Long postId) {
        String key = generateKey(postId);
        return postLikeRedisTemplate.opsForValue().increment(key);
    }
    public Long decrement(Long postId) {
        String key = generateKey(postId);
        return postLikeRedisTemplate.opsForValue().decrement(key);
    }
    public Long getCurrentPostLikeCount(Long postId) {
        String key = generateKey(postId);
        String result = postLikeRedisTemplate.opsForValue().get(key);
        return result != null ? Long.parseLong(result) : 0L;
    }

    private String generateKey(Long postId) {
        return KEY.formatted(postId);
    }
}
