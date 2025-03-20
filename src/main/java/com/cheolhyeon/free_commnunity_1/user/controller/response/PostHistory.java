package com.cheolhyeon.free_commnunity_1.user.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostHistory {
    @JsonIgnore
    private Long postId;
    private String title;
    private Long viewCount;

    public static PostHistory of(Long postId, String title, Long currentViewCount) {
        PostHistory postHistory = new PostHistory();
        postHistory.postId = postId;
        postHistory.title = title;
        postHistory.viewCount = currentViewCount;
        return postHistory;
    }
}
