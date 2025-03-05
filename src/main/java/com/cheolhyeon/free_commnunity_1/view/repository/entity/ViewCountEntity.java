package com.cheolhyeon.free_commnunity_1.view.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "post_view_count")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostViewCountEntity {
    @Id
    private Long postId;
    private Long viewCount;

    public static PostViewCountEntity init(Long postId, Long viewCount) {
        return new PostViewCountEntity(postId, viewCount);
    }
}
