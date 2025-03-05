package com.cheolhyeon.free_commnunity_1.post.controller.response;


import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostReadResponse {
    private String title;
    private String content;
    private String nickname;
    private String imageUrl;
    private String categoryName;
    private Long viewCount;
    private LocalDateTime updatedAt;

    public static PostReadResponse from(Post post, Long currentViewCount, String writer, String categoryName) {
        PostReadResponse response = new PostReadResponse();
        response.title = post.getTitle();
        response.content = post.getContent();
        response.nickname = writer;
        response.categoryName = categoryName;
        response.viewCount = currentViewCount;
        response.updatedAt = post.getUpdatedAt();
        return response;
    }
}
