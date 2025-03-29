package com.cheolhyeon.free_commnunity_1.commentlike.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public Map<Long, Long> getCurrentCommentLikeCount(Long postId, List<Long> commentIds) {
        List<String> keys = commentIds.stream()
                .map(commentId -> generateKey(postId, commentId))
                .toList();

        List<String> results = commentLikeRedisTemplate.opsForValue().multiGet(keys);

        Map<Long, Long> likeCounts = new HashMap<>();
        for (int i = 0; i < commentIds.size(); i++) {
            Long commentId = commentIds.get(i);
            String raw = Objects.requireNonNull(results).get(i);
            Long count = (raw == null) ? 0L : Long.parseLong(raw);
            likeCounts.put(commentId, count);
        }
        return likeCounts;
    }

    private String generateKey(Long postId, Long commentId) {
        return KEY.formatted(postId, commentId);
    }

}
