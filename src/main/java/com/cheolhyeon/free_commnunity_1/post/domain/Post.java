package com.cheolhyeon.free_commnunity_1.post.domain;


import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String imageUrl;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Post from(PostCreateRequest request, Long userId, String imageAsJsonString) {
        Post post = new Post();
        post.categoryId = request.getCategoryId();
        post.title = request.getTitle();
        post.content = request.getContent();
        post.imageUrl = imageAsJsonString;
        post.createdAt = LocalDateTime.now();
        post.updatedAt = LocalDateTime.now();
        post.userId = userId;
        return post;
    }
}

