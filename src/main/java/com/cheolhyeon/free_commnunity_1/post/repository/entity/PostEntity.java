package com.cheolhyeon.free_commnunity_1.post.repository.entity;


import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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

    public static PostEntity from(Post post) {
        PostEntity entity = new PostEntity();
        entity.title = post.getTitle();
        entity.content = post.getContent();
        entity.userId = post.getUserId();
        entity.imageUrl = post.getImageUrl();
        entity.categoryId = post.getCategoryId();
        entity.createdAt = post.getCreatedAt();
        entity.updatedAt = post.getUpdatedAt();
        return entity;
    }

    public Post toModel() {
        return Post.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .userId(this.userId)
                .imageUrl(this.imageUrl)
                .categoryId(this.categoryId)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
