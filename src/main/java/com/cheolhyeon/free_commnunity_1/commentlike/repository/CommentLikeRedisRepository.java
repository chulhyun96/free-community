package com.cheolhyeon.free_commnunity_1.commentlike.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentLikeRedisRepository {
    private static final String KEY = "posts:%s:comment:%s:like";
    private final StringRedisTemplate commentLikeRedisTemplate;

    public Long increment(Long postId, Long commentId) {
        String key = generateKey(postId, commentId);
        return commentLikeRedisTemplate.opsForValue().increment(key);
    }

    public Long decrement(Long postId, Long commentId) {
        String key = generateKey(postId, commentId);
        return commentLikeRedisTemplate.opsForValue().decrement(key);
    }

    public Long getCurrentCommentLikeCount(Long postId,Long commentId) {
        String key = generateKey(postId, commentId);
        String result = commentLikeRedisTemplate.opsForValue().get(key);
        return result == null ? 0L : Long.parseLong(result);
    }

    private String generateKey(Long postId,Long commentId) {
        return KEY.formatted(postId, commentId);
    }

}
