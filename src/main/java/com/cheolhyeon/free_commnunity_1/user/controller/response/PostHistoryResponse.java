package com.cheolhyeon.free_commnunity_1.user.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostHistoryResponse {
    private Long postId;
    private String title;
    private Long viewCount;

    public static PostHistoryResponse of(Long postId, String title, Long currentViewCount) {
        PostHistoryResponse postHistoryResponse = new PostHistoryResponse();
        postHistoryResponse.postId = postId;
        postHistoryResponse.title = title;
        postHistoryResponse.viewCount = currentViewCount;
        return postHistoryResponse;
    }
}
