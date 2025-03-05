package com.cheolhyeon.free_commnunity_1.comment.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;


@ToString
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    private Long commentId;
    private String content;
    private Long postId;
    private Long parentCommentId;
    private Long userId;
    private Boolean deleted;
    private LocalDateTime createdAt;


    public boolean isRoot() {
        return Objects.equals(parentCommentId, commentId);
    }

    public boolean delete() {
        return deleted = true;
    }
}
