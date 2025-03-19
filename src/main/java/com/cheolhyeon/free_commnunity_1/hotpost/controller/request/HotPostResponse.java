package com.cheolhyeon.free_commnunity_1.hotpost.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class HotPostResponse {
    private String title;
    private Long currentViewCount;
    private Long currentPostLikeCount;
    private Long totalScore;

    public static HotPostResponse from(String title, Long currentViewCount, Long currentPostLikeCount) {
        HotPostResponse hotPostResponse = new HotPostResponse();
        hotPostResponse.title = title;
        hotPostResponse.currentViewCount = currentViewCount;
        hotPostResponse.currentPostLikeCount = currentPostLikeCount;
        return hotPostResponse;
    }

    public void allocateTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }
}
