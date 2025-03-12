package com.cheolhyeon.free_commnunity_1.commentlike.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "comment_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLikeEntity {
    @Id
    private Long commentId;
    private Long commentLikeCount;
}
