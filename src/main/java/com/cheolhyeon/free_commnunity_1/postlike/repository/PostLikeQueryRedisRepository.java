package com.cheolhyeon.free_commnunity_1.postlike.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostLikeQueryRedisRepository {
    private static final String TOGGLE_KEY = "post:%s:users:%s:toggle";
    private final StringRedisTemplate postLikeRedisTemplate;

    public boolean isLikedByUserIdAndPostId(Long postId, Long userId) {
        String key = generateKey(postId, userId);
        return Boolean.TRUE.equals(postLikeRedisTemplate.hasKey(key));
    }

    public void insert(Long postId, Long userId) {
        String key = generateKey(postId, userId);
        postLikeRedisTemplate.opsForValue().set(key, "");
    }

    public void delete(Long postId, Long userId) {
        String key = generateKey(postId, userId);
        postLikeRedisTemplate.delete(key);
    }

    private String generateKey(Long postId, Long userId) {
        return TOGGLE_KEY.formatted(postId, userId);
    }
}
