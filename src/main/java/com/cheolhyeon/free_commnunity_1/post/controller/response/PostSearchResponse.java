package com.cheolhyeon.free_commnunity_1.post.controller.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchResponse {
    private Long postId;
    private String title;
    private String categoryName;
    private Long viewCount;
    private LocalDateTime createdAt;

    public void allocateCurrentViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
}
