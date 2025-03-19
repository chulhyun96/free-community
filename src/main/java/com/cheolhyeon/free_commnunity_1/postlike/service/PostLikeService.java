package com.cheolhyeon.free_commnunity_1.postlike.service;

import com.cheolhyeon.free_commnunity_1.postlike.repository.PostLikeQueryRedisRepository;
import com.cheolhyeon.free_commnunity_1.postlike.repository.PostLikeRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRedisRepository likeRedisRepository;
    private final PostLikeQueryRedisRepository queryRedisRepository;

    public Long toggleLike(Long postId, Long userId) {
        boolean isAlreadyLiked = queryRedisRepository.isLikedByUserIdAndPostId(postId, userId);
        if (isAlreadyLiked) {
            return unlikePost(postId, userId);
        }
        return likePost(postId, userId);
    }

        public Long getCurrentPostLikeCount(Long postId) {
        return likeRedisRepository.getCurrentPostLikeCount(postId);
    }

    private Long likePost(Long postId, Long userId) {
        queryRedisRepository.insert(postId, userId);
        return likeRedisRepository.increment(postId);
    }

    private Long unlikePost(Long postId, Long userId) {
        queryRedisRepository.delete(postId, userId);
        return likeRedisRepository.decrement(postId);
    }
}
