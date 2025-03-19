package com.cheolhyeon.free_commnunity_1.hotpost.service;


import org.springframework.stereotype.Component;

@Component
public class HotPostScoreCalculator {
    private static final int VIEW_POINT = 1;
    private static final int LIKE_POINT = 2;

    public Long calculateScore(Long viewCount, Long likeCount) {
        return (viewCount * VIEW_POINT) +  (likeCount * LIKE_POINT);
    }
}
