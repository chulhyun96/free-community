package com.cheolhyeon.free_commnunity_1.commentlike.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentLikeQueryRedisRepository {
    private static final String KEY = "comments_like:users:%s:comments:%s";
    private final StringRedisTemplate commentLikeRedisTemplate;

    public boolean isLikedByUserIdAndCommentId(Long userId, Long commentId) {
        String key = generateKey(userId, commentId);
        return contains(key);
    }
    public void insert(Long userId, Long commentId) {
        String key = generateKey(userId, commentId);
        commentLikeRedisTemplate.opsForValue().set(key, "");
    }

    public void delete(Long userId, Long commentId) {
        String key = generateKey(userId, commentId);
        commentLikeRedisTemplate.delete(key);
    }

    private boolean contains(String key) {
        return Boolean.TRUE.equals(commentLikeRedisTemplate.hasKey(key));
    }

    private String generateKey(Long userId, Long commentId) {
        return KEY.formatted(userId, commentId);
    }
}
