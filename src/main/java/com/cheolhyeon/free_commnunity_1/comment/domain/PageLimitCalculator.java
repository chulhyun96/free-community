package com.cheolhyeon.free_commnunity_1.comment.domain;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class PageLimitCalculator {

    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
        return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
    }
}
