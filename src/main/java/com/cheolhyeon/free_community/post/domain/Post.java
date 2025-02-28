package com.cheolhyeon.free_community.post.domain;

import com.cheolhyeon.free_community.post.controller.request.PostCreateRequest;
import lombok.*;

import java.time.LocalDateTime;

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

    public static Post from(PostCreateRequest request, String imageUrl) {
        Post post = new Post();
        post.categoryId = request.getCategoryId();
        post.userId = request.getWriterId();
        post.title = request.getTitle();
        post.content = request.getContent();
        post.imageUrl = imageUrl;
        post.createdAt = LocalDateTime.now();
        post.updatedAt = LocalDateTime.now();
        return post;
    }
}

