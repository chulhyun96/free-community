package com.cheolhyeon.free_commnunity_1.report.repository;

import com.cheolhyeon.free_commnunity_1.user.repository.RedisUserBanRepository;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RedisUserBanRepositoryTest {
    @Mock
    StringRedisTemplate reportRedisTemplate;

    @Mock
    ValueOperations<String, String> operations;

    @InjectMocks
    RedisUserBanRepository redisUserBanRepository;

    @Test
    @DisplayName("해당 UserId의 key가 없을 경우 key를 생성하고 ban에 등록하고 true를 반환한다..")
    void banIfKeyIsAbsent() {
        //given
        given(reportRedisTemplate.opsForValue()).willReturn(operations);
        given(operations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .willReturn(true);
        //when
        boolean result = redisUserBanRepository.ban(1L, Duration.ofDays(3));

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("해당 UserId의 key가 이미 있을 경우 false를 반환한다.")
    void banIfKeyIsPresent() {
        //given
        given(reportRedisTemplate.opsForValue()).willReturn(operations);
        given(operations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .willReturn(false);
        //when
        boolean result = redisUserBanRepository.ban(1L, Duration.ofDays(3));

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이미 ban으로 등록이 되었다면 true를 반환한다.")
    void alreadyBanned() {
        //given
        given(reportRedisTemplate.hasKey(anyString()))
                .willReturn(true);
        //when
        boolean result = redisUserBanRepository.isUserBanned(1L);
        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("ban으로 등록이 되지 않았다면 false를 반환한다.")
    void notBanned() {
        //given
        given(reportRedisTemplate.hasKey(anyString()))
                .willReturn(false);
        //when
        boolean result = redisUserBanRepository.isUserBanned(1L);
        //then
        assertThat(result).isFalse();
    }

}