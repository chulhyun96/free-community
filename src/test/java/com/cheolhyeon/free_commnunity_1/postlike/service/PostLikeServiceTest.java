package com.cheolhyeon.free_commnunity_1.postlike.service;

import com.cheolhyeon.free_commnunity_1.postlike.repository.PostLikeQueryRedisRepository;
import com.cheolhyeon.free_commnunity_1.postlike.repository.PostLikeRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @Mock
    PostLikeRedisRepository likeRedisRepository;

    @Mock
    PostLikeQueryRedisRepository queryRedisRepository;

    @InjectMocks
    PostLikeService postLikeService;

    @Test
    @DisplayName("특정 유저가 특정 게시글에 처음 좋아요를 누를 경우 키를 저장하고 좋아요 수가 증가한다.")
    void toggleLikePost() {
        //given
        given(queryRedisRepository.isLikedByUserIdAndPostId(anyLong(), anyLong())).willReturn(false);
        given(likeRedisRepository.increment(anyLong())).willReturn(1L);
        //when
        Long likeCount = postLikeService.toggleLike(1L, 1L);
        //then
        assertThat(likeCount).isEqualTo(1L);
        then(likeRedisRepository).should(times(1)).increment(anyLong());
        then(queryRedisRepository).should(times(1)).insert(anyLong(), anyLong());
    }

    @Test
    @DisplayName("특정 유저가 특정 게시글에 또 다시 좋아요를 누를 경우 키를 삭제하고 좋아요 수가 감소한다.")
    void toggleUnlikePost() {
        //given
        given(queryRedisRepository.isLikedByUserIdAndPostId(anyLong(), anyLong()))
                .willReturn(true);
        given(likeRedisRepository.decrement(anyLong())).willReturn(1L);
        //when
        Long likeCount = postLikeService.toggleLike(1L, 1L);
        //then
        assertThat(likeCount).isEqualTo(1L);
        then(likeRedisRepository).should(times(1)).decrement(anyLong());
        then(queryRedisRepository).should(times(1)).delete(anyLong(), anyLong());
    }
    @Test
    @DisplayName("특정 게시글의 현재 좋아요 수를 가지고 온다")
    void getCurrentPostLikeCount() {
        //given
        given(postLikeService.getCurrentPostLikeCount(anyLong()))
                .willReturn(1L);
        //when
        Long likeCount = postLikeService.getCurrentPostLikeCount(5L);
        //then
        assertThat(likeCount).isEqualTo(1L);
    }
}