package com.cheolhyeon.free_commnunity_1.view.service;

import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountDistributedLockRepository;
import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ViewCountServiceTest {

    @Mock
    ViewCountRedisRepository viewCountRedisRepository;

    @Mock
    ViewCountBackUpService viewCountBackUpService;

    @Mock
    ViewCountDistributedLockRepository viewCountDistributedLockRepository;

    @InjectMocks
    ViewCountService viewCountService;


    @Test
    @DisplayName(" 락이 걸리면 viewCountRedisRepository.read()가 호출되고 증가하지 않는다.")
    void shouldReturnCurrentViewCountIfLockIsAcquired() {
        // Given
        Long postId = 1L;
        Long userId = 2L;
        given(viewCountDistributedLockRepository.lock(postId, userId, Duration.ofMinutes(5))).willReturn(true);
        given(viewCountRedisRepository.read(postId)).willReturn(100L);

        // When
        Long result = viewCountService.increase(postId, userId);

        // Then
        assertThat(result).isEqualTo(100L);
        verify(viewCountRedisRepository, never()).increase(anyLong());
        verify(viewCountBackUpService, never()).backUp(anyLong(), anyLong());
    }

    @Test
    @DisplayName("락이 없으면 viewCount가 증가하며 backUp은 호출되지 않는다.")
    void shouldIncreaseViewCountWithoutBackupIfNotMultipleOfBatchSize() {
        // Given
        Long postId = 1L;
        Long userId = 2L;
        given(viewCountDistributedLockRepository.lock(postId, userId, Duration.ofMinutes(5))).willReturn(false);
        given(viewCountRedisRepository.increase(postId)).willReturn(7L);

        // When
        Long result = viewCountService.increase(postId, userId);

        // Then
        assertThat(result).isEqualTo(7L);
        verify(viewCountRedisRepository, times(1)).increase(postId);
        verify(viewCountBackUpService, never()).backUp(anyLong(), anyLong());
    }

    @Test
    @DisplayName("증가된 viewCount가 BACK_UP_BATCH_SIZE의 배수이면 backUp이 호출된다.")
    void shouldCallBackupWhenViewCountIsMultipleOfBatchSize() {
        // Given
        Long postId = 1L;
        Long userId = 2L;
        given(viewCountDistributedLockRepository.lock(postId, userId, Duration.ofMinutes(5))).willReturn(false);
        given(viewCountRedisRepository.increase(postId)).willReturn(50L);

        // When
        Long result = viewCountService.increase(postId, userId);

        // Then
        assertThat(result).isEqualTo(50L);
        verify(viewCountRedisRepository, times(1)).increase(postId);
        verify(viewCountBackUpService, times(1)).backUp(postId, result);
    }

    @Test
    @DisplayName("특정 게시글의 현재 조회수를 반환한다")
    void getCurrentViewCount() {
        //given
        given(viewCountRedisRepository.read(anyLong()))
                .willReturn(50L);
        //when
        Long currentViewCount = viewCountService.getCurrentViewCount(1L);

        //then
        assertThat(currentViewCount).isEqualTo(50L);

    }
}
