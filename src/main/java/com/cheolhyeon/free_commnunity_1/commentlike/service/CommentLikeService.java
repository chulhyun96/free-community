package com.cheolhyeon.free_commnunity_1.commentlike.service;

import com.cheolhyeon.free_commnunity_1.commentlike.repository.CommentLikeQueryRedisRepository;
import com.cheolhyeon.free_commnunity_1.commentlike.repository.CommentLikeRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRedisRepository commentLikeRedisRepository;
    private final CommentLikeQueryRedisRepository commentLikeQueryRedisRepository;

    public Long toggleLike(Long userId, Long postId, Long commentId) {
        boolean isAlreadyLiked = commentLikeQueryRedisRepository.isLikedByUserIdAndCommentId(userId, commentId);
        if (isAlreadyLiked) {
            return unlikeComment(userId, commentId, postId);
        }
        return likeComment(userId, commentId, postId);
    }

    public Long getCurrentCommentLikeCount(Long postId, Long commentId) {
        return commentLikeRedisRepository.getCurrentCommentLikeCount(postId, commentId);
    }

    private Long likeComment(Long userId, Long commentId, Long postId) {
        commentLikeQueryRedisRepository.insert(userId, commentId);
        return commentLikeRedisRepository.increment(postId, commentId);
    }

    private Long unlikeComment(Long userId, Long commentId, Long postId) {
        commentLikeQueryRedisRepository.delete(userId, commentId);
        return commentLikeRedisRepository.decrement(postId, commentId);
    }
}
