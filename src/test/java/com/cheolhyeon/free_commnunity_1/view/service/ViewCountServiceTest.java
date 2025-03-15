package com.cheolhyeon.free_commnunity_1.view.service;

import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountDistributedLockRepository;
import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

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
    @DisplayName("유저가 게시글 조회 시 lock이 걸려있지 않으면 조회수를 증가시킨다")
    void increaseWithoutLock() {
        //given
        given(viewCountDistributedLockRepository.lock(anyLong(), anyLong(), any(Duration.class)))
                    .willReturn(Boolean.FALSE);
        given(viewCountRedisRepository.increase(anyLong()))
                .willReturn(1L);
        //when
        Long increase = viewCountService.increase(1L, 1L);
        //then
        Assertions.assertThat(increase).isEqualTo(1L);
    }
    @Test
    @DisplayName("유저가 게시글 조회 시 lock이 걸려있다면 조회수를 증가시키지 않고 해당 게시글의 최신 조회수를 반환한다.")
    void increaseWithLock() {
        //given
        given(viewCountDistributedLockRepository.lock(anyLong(), anyLong(), any(Duration.class)))
                .willReturn(Boolean.TRUE);
        given(viewCountRedisRepository.read(anyLong()))
                .willReturn(1L);
        //when
        Long currentViewCount = viewCountService.increase(1L, 1L);
        //then
        Assertions.assertThat(currentViewCount).isEqualTo(1L);
    }
}
