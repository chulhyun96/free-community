package com.cheolhyeon.free_commnunity_1.report.service;

import com.cheolhyeon.free_commnunity_1.report.repository.ReportRedisRepository;
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
        given(operations.setIfAbsent(anyString(),anyString(), any(Duration.class)))
                .willReturn(true);
        given(operations.increment(key))
                .willReturn(1L);
        //when
        Long result = reportRedisRepository.report(1L, ttl);

        //then
        assertThat(result).isEqualTo(1L);
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
        given(operations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .willReturn(false);
        given(operations.increment(key))
                .willReturn(1L);
        //when
        Long result = reportRedisRepository.report(1L, ttl);

        //then
        assertThat(result).isEqualTo(1L);
        then(operations)
                .should(times(1))
                .increment(key);
        then(operations).shouldHaveNoMoreInteractions();
    }
    @Test
    @DisplayName("특정 유저의 신고 횟수를 가지고 오는데 신고 회수가 없을 경우 0을 반환한다.")
    void getReportCountReturnZero() {
        //given
        given(template.opsForValue()).willReturn(operations);
        given(operations.get(anyString()))
                .willReturn(null);
        //when
        Long reportCount = reportRedisRepository.getReportCount(1L);
        //then
        assertThat(reportCount).isZero();
    }
    @Test
    @DisplayName("특정 유저의 최근 신고 회수를 가지고 온다.")
    void getReportCountReturnCurrentCount() {
        //given
        given(template.opsForValue()).willReturn(operations);
        given(operations.get(anyString()))
                .willReturn("5");
        //when
        Long reportCount = reportRedisRepository.getReportCount(1L);
        //then
        assertThat(reportCount).isEqualTo(5);
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