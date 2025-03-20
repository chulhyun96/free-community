package com.cheolhyeon.free_commnunity_1.common.domain;

import java.time.LocalDateTime;

public class DateManager {
    private final LocalDateTime localDateTime;

    public DateManager(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getMinusMonthsAsLocalDate(long months) {
        return localDateTime.toLocalDate().minusMonths(months).atStartOfDay();
    }

    public LocalDateTime getLocalDateNow() {
        return localDateTime.toLocalDate().atStartOfDay();
    }
}
