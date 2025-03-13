package com.cheolhyeon.free_commnunity_1.commentlike.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CommentLikeRedisRepositoryTest {

    @Mock
    private StringRedisTemplate commentLikeRedisTemplate;

    @InjectMocks
    private CommentLikeRedisRepository likeRedisRepository;

    @Mock
    ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("댓글의 좋아요 개수가 증가해야 한다.")
    void shouldIncrementLikeCount() {
        // Given
        given(commentLikeRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(anyString())).willReturn(1L);
        given(valueOperations.get(anyString())).willReturn("1");

        // When
        likeRedisRepository.increment(1L, 1L);
        Long currentCommentLikeCount = likeRedisRepository.getCurrentCommentLikeCount(1L, 1L);

        // Then
        assertThat(currentCommentLikeCount).isEqualTo(1L);
        then(valueOperations).should().increment(anyString());
    }
    @Test
    @DisplayName("댓글의 좋아요 개수가 감소해야 한다.")
    void shouldDecrementLikeCount() {
        // Given
        given(commentLikeRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.decrement(anyString())).willReturn(1L);
        given(valueOperations.get(anyString())).willReturn("1");

        // When
        likeRedisRepository.decrement(1L,1L);
        Long currentCommentLikeCount = likeRedisRepository.getCurrentCommentLikeCount(1L,1L);

        // Then
        assertThat(currentCommentLikeCount).isEqualTo(1L);
        then(valueOperations).should().decrement(anyString());
    }

    @Test
    @DisplayName("현재 특정 Comment의 좋아요 개수를 조회한다")
    void getCurrentCommentLikeCount() {
        //given
        given(commentLikeRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("100");
        //when
        Long currentCommentLikeCount = likeRedisRepository.getCurrentCommentLikeCount(1L,1L);
        //then
        assertThat(currentCommentLikeCount).isEqualTo(100);
        then(valueOperations).should(times(1)).get(anyString());
    }
}