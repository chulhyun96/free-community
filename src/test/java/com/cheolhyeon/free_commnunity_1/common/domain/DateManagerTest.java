package com.cheolhyeon.free_commnunity_1.common.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateManagerTest {

    @Test
    @DisplayName("getMinusMonthsAsLocalDate 함수에 1을 넣으면 오늘 날짜를 기준 1달 전 날짜 데이터가 출력된다")
    void doTest() {
        //given
        DateManager dateManager = new DateManager(LocalDateTime.of(2025, 3, 1, 0, 0));

        //when
        LocalDateTime minusMonthsAsLocalDate = dateManager.getMinusMonthsAsLocalDate(1);

        //then
        assertThat(minusMonthsAsLocalDate).isEqualTo(LocalDateTime.of(2025, 2, 1, 0, 0));
    }

}