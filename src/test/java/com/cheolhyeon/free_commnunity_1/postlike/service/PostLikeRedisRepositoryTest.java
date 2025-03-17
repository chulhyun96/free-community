package com.cheolhyeon.free_commnunity_1.postlike.service;

import com.cheolhyeon.free_commnunity_1.postlike.repository.PostLikeRedisRepository;
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

@ExtendWith(MockitoExtension.class)
class PostLikeRedisRepositoryTest {

    @Mock
    StringRedisTemplate template;

    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    PostLikeRedisRepository rep;

    @Test
    @DisplayName("특정 게시글의 좋아요를 증가시킨다")
    void increment() {
        //given
        given(template.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(anyString())).willReturn(1L);
        //when
        Long increment = rep.increment(1L);
        //then
        assertThat(increment).isEqualTo(1L);
    }

    @Test
    @DisplayName("특정 게시글의 좋아요를 감소 시킨다")
    void decrement() {
        //given
        given(template.opsForValue()).willReturn(valueOperations);
        given(valueOperations.decrement(anyString())).willReturn(2L);

        //when
        Long result = rep.decrement(1L);

        //then
        assertThat(result).isEqualTo(2L);
    }
    @Test
    @DisplayName("특정 게시글의 현재 좋아요 수를 가지고 온다")
    void getCurrentPostLike() {
        //given
        given(template.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("1");
        //when
        Long currentPostLikeCount = rep.getCurrentPostLikeCount(1L);
        //then
        assertThat(currentPostLikeCount).isEqualTo(1L);
    }
    @Test
    @DisplayName("특정 게시글의 현재 좋아요 수가 null 일 경우 0을 반환한다")
    void getCurrentPostLikeWhenNull() {
        //given
        given(template.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn(null);
        //when
        Long currentPostLikeCount = rep.getCurrentPostLikeCount(1L);
        //then
        assertThat(currentPostLikeCount).isZero();
    }
}