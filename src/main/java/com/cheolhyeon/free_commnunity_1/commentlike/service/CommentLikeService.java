package com.cheolhyeon.free_commnunity_1.commentlike.service;

import com.cheolhyeon.free_commnunity_1.commentlike.repository.CommentLikeQueryRedisRepository;
import com.cheolhyeon.free_commnunity_1.commentlike.repository.CommentLikeRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRedisRepository commentLikeRedisRepository;
    private final CommentLikeQueryRedisRepository commentLikeQueryRedisRepository;

    @Transactional
    public Long toggleLike(Long userId, Long commentId) {
        boolean isAlreadyLiked = commentLikeQueryRedisRepository.isLikedByUserIdAndCommentId(userId, commentId);
        if (isAlreadyLiked) {
            return unlikeComment(userId, commentId);
        }
        return likeComment(userId, commentId);
    }

    @Transactional(readOnly = true)
    public Long getCurrentCommentLikeCount(Long commentId) {
        return commentLikeRedisRepository.getCurrentCommentLikeCount(commentId);
    }

    private Long likeComment(Long userId, Long commentId) {
        commentLikeQueryRedisRepository.insert(userId, commentId);
        return commentLikeRedisRepository.increment(commentId);
    }

    private Long unlikeComment(Long userId, Long commentId) {
        commentLikeQueryRedisRepository.delete(userId, commentId);
        return commentLikeRedisRepository.decrement(commentId);
    }
}
