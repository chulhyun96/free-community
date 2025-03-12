package com.cheolhyeon.free_commnunity_1.view.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ViewCountRedisRepositoryTest {

    @Mock
    StringRedisTemplate viewCountRedisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    ViewCountRedisRepository viewCountRedisRepository;

    String key = "post:1:view_count";
    Long postId = 1L;

    @Test
    @DisplayName("특정 게시글 조회수 조회")
    void readPostViewCount() {
        // Given
        given(viewCountRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn("100");

        // When
        Long result = viewCountRedisRepository.read(postId);

        // Then
        assertThat(result).isEqualTo(100L);
    }

    @Test
    @DisplayName("특정 게시글 조회 시 조회수가 null일 경우")
    void readPostViewCountNull() {
        //given
        given(viewCountRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn(null);
        //when
        Long result = viewCountRedisRepository.read(postId);
        //then
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("특정 게시글 조회시 조회수 증가")
    void doTest() {
        //given
        given(viewCountRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(key)).willReturn(101L);
        //when
        Long increase = viewCountRedisRepository.increase(postId);

        //then
        assertThat(increase).isEqualTo(101L);
    }

    @Test
    @DisplayName("특정 게시글 조회 시 첫 조회일 경우")
    void readPostViewCountIsFirst() {
        //given
        given(viewCountRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn(null);
        given(valueOperations.increment(key)).willReturn(1L);

        //when
        Long result = viewCountRedisRepository.read(postId);
        Long increase = viewCountRedisRepository.increase(postId);

        //then
        assertThat(result).isZero();
        assertThat(increase).isEqualTo(1L);
    }
    @Test
    @DisplayName("특정 게시글 5번 조회")
    void readPostViewCountConstituent() {
        //given
        given(viewCountRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn("50");
        given(valueOperations.increment(key)).willReturn(55L);

        //when
        Long result = viewCountRedisRepository.read(postId);
        Long increase = null;
        for (int i = 0; i < 5; i++) {
           increase = viewCountRedisRepository.increase(postId);
        }

        //then
        assertThat(result).isEqualTo(50L);
        assertThat(increase).isEqualTo(55L);
    }
}