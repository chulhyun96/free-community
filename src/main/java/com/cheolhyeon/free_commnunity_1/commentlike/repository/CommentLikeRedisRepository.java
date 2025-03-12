package com.cheolhyeon.free_commnunity_1.commentlike.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentLikeRedisRepository {
    private static final String KEY = "comments_like:%s:count";
    private final StringRedisTemplate commentLikeRedisTemplate;

    public void increment(Long commentId) {
        String key = generateKey(commentId);
        commentLikeRedisTemplate.opsForValue().increment(key);
    }

    public void decrement(Long commentId) {
        String key = generateKey(commentId);
        commentLikeRedisTemplate.opsForValue().decrement(key);
    }

    public Long getCurrentCommentLikeCount(Long commentId) {
        String key = generateKey(commentId);
        String result = commentLikeRedisTemplate.opsForValue().get(key);
        return result == null ? 0L : Long.parseLong(result);
    }

    private String generateKey(Long commentId) {
        return KEY.formatted(commentId);
    }
}
