package com.cheolhyeon.free_commnunity_1.report.service;

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
class ReportRedisRepositoryTest {
    @Mock
    StringRedisTemplate template;

    @Mock
    ValueOperations<String, String> operations;

    @InjectMocks
    ReportRedisRepository reportRedisRepository;

    @Test
    @DisplayName("key가 존재하지 않으면 key를 추가하고 value를 1증가 시킨다.")
    void reportWhenHasKey() {
        //given
        String key = "report:users:1";
        Duration ttl = Duration.ofDays(1);
        given(template.opsForValue()).willReturn(operations);
        given(template.hasKey(key)).willReturn(false);
        given(operations.increment(key))
                .willReturn(1L);
        //when
        Long result = reportRedisRepository.report(1L, ttl);

        //then
        assertThat(result).isEqualTo(1L);
        then(operations)
                .should(times(1))
                .set(key, "0", ttl);
        then(operations)
                .should(times(1))
                .increment(key);
    }

    @Test
    @DisplayName("key가 존재하면 value를 1증가 시킨다.")
    void reportWhenDoesNotHaveKey() {
        //given
        String key = "report:users:1";
        Duration ttl = Duration.ofDays(1);
        given(template.opsForValue()).willReturn(operations);
        given(template.hasKey(key)).willReturn(true);
        given(operations.increment(key))
                .willReturn(2L);
        //when
        Long result = reportRedisRepository.report(1L, ttl);

        //then
        assertThat(result).isEqualTo(2L);
        then(operations).should(never()).set(anyString(), anyString(), any(Duration.class));
        then(operations)
                .should(times(1))
                .increment(key);
        then(operations).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("UserId를 받아 동적으로 key를 생성한다")
    void generateKey() {
        //given
        final String KEY = "report:users:%s";
        Long userId = 1L;

        //when
        String formatted = KEY.formatted(userId);

        //then
        assertThat(formatted).isEqualTo("report:users:1");
    }
}