package com.cheolhyeon.free_community.post.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponse {
    private String categoryName;
    private Long userId;
    private LocalDateTime createdAt;
    private String title;
    private String content;
    // private List<ImageConverter> images;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    // private List<Comment> comments = new ArrayList or HashSet -> 작성 날짜를 기반으로, 처음에는 empty
}
