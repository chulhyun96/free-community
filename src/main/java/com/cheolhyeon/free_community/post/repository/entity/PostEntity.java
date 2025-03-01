package com.cheolhyeon.free_community.post.repository.entity;

import com.cheolhyeon.free_community.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String imageUrl;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostEntity from(Post newPost) {
        PostEntity entity = new PostEntity();
        entity.title = newPost.getTitle();
        entity.content = newPost.getContent();
        entity.userId = newPost.getUserId();
        entity.imageUrl = newPost.getImageUrl();
        entity.categoryId = newPost.getCategoryId();
        entity.createdAt = newPost.getCreatedAt();
        entity.updatedAt = newPost.getUpdatedAt();
        return entity;
    }

    public Post toModel() {
        return Post.builder()
                .id(id)
                .title(title)
                .content(content)
                .userId(userId)
                .imageUrl(imageUrl)
                .categoryId(categoryId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
