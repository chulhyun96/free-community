package com.cheolhyeon.free_commnunity_1.view.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
class ViewCountDistributedLockRepositoryTest {
    @Mock
    StringRedisTemplate viewCountRedisTemplate;

    @Mock
    ValueOperations<String,String> valueOperations;

    @InjectMocks
    ViewCountDistributedLockRepository viewCountDistributedLockRepository;

    String key = "post:1:user:1:lock";
    Long postId = 1L;
    Long userId = 1L;
    Duration ttl = Duration.ofMinutes(3);

    @Test
    @DisplayName("조회수 어뷰징 방지 - 이미 조회한 유저가 또 조회할 경우")
    void abusingPolicyTrue() {
        //given
        given(viewCountRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(key,"",ttl)).willReturn(true);
        //when
        boolean lock = viewCountDistributedLockRepository.lock(postId, userId, ttl);

        //then
        assertFalse(lock);
        then(valueOperations).should().setIfAbsent(key, "", ttl);
    }
    @Test
    @DisplayName("조회수 어뷰징 방지 - 처음 조회할 경우")
    void abusingPolicyFalse() {
        //given
        given(viewCountRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(key,"",ttl)).willReturn(false);
        //when
        boolean lock = viewCountDistributedLockRepository.lock(postId, userId, ttl);

        //then
        assertTrue(lock);
        then(valueOperations).should().setIfAbsent(key, "", ttl);
    }
    @Test
    @DisplayName("키 생성")
    void generateKey() {
        // Given
        String expectedKey = "post:1:user:1:lock";

        // When
        String actualKey = expectedKey.formatted(1L, 1L);

        // Then
        assertThat(actualKey).isEqualTo(expectedKey);

    }
}