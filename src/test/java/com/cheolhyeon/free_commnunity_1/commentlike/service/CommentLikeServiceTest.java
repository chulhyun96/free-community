package com.cheolhyeon.free_commnunity_1.commentlike.service;

import com.cheolhyeon.free_commnunity_1.commentlike.repository.CommentLikeQueryRedisRepository;
import com.cheolhyeon.free_commnunity_1.commentlike.repository.CommentLikeRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {
    @Mock
    CommentLikeRedisRepository commentLikeRedisRepository;

    @Mock
    CommentLikeQueryRedisRepository commentLikeQueryRedisRepository;

    @InjectMocks
    CommentLikeService commentLikeService;

    private final Long userId = 1L;
    private final Long commentId = 1L;

    @Test
    @DisplayName("처음에 댓글 좋아요를 누르면 Redis에 키를 저장하고 댓글 좋아요 수가 증가한다.")
    void incrementCommentLikeWhenItsFirst() {
        //given
        Long postId = 1L;
        given(commentLikeQueryRedisRepository.isLikedByUserIdAndCommentId(
                userId, commentId)).willReturn(false);
        //when
        commentLikeService.toggleLike(userId, postId, commentId);

        //then
        then(commentLikeRedisRepository).should(times(1)).increment(postId,commentId);
        then(commentLikeQueryRedisRepository).should(times(1)).insert(userId, commentId);
    }

    @Test
    @DisplayName("이미 좋아요를 누른 댓글에 또 좋아요를 누르면 Redis에서 키를 삭제하고 댓글 좋아요 수가 감소한다.")
    void decrementCommentLikeWhenSecond() {
        //given
        Long postId = 1L;
        given(commentLikeQueryRedisRepository.isLikedByUserIdAndCommentId(
                userId, commentId)).willReturn(true);
        //when
        commentLikeService.toggleLike(userId, postId, commentId);

        //then
        then(commentLikeRedisRepository).should(times(1)).decrement(postId, commentId);
        then(commentLikeQueryRedisRepository).should(times(1)).delete(userId, commentId);
    }

    @Test
    @DisplayName("특정 댓글의 전체 좋아요 개수를 반환한다.")
    void doTest() {
        //given
        Long postId = 1L;
        given(commentLikeService.getCurrentCommentLikeCount(postId, commentId)).willReturn(100L);
        //when
        commentLikeService.getCurrentCommentLikeCount(postId, commentId);
        //then
        then(commentLikeRedisRepository).should(times(1)).getCurrentCommentLikeCount(postId, commentId);
    }
}