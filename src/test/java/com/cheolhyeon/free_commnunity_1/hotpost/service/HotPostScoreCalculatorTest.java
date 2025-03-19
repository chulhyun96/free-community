package com.cheolhyeon.free_commnunity_1.hotpost.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HotPostScoreCalculatorTest {

    @Mock
    HotPostScoreCalculator hotPostScoreCalculator;

    @Test
    @DisplayName("조회수는 1점, 좋아요는 2점으로 점수를 계산한다")
    void calculateScore() {
        //given
        Long result = (long) ((100 * 1) + (30 * 2));
        given(hotPostScoreCalculator.calculateScore(100L, 30L))
                .willReturn(result);
        //when
        Long score = hotPostScoreCalculator.calculateScore(100L, 30L);

        //then
        Assertions.assertThat(result).isEqualTo(score);
    }
}