package com.cheolhyeon.free_commnunity_1.comment.repository.entity;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private Long parentCommentId;
    private Long postId;
    private Long userId;
    private String content;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentEntity from(CommentCreateRequest request, Comment parentComment) {
        CommentEntity entity = new CommentEntity();
        entity.parentCommentId = parentComment == null ? 0L : parentComment.getParentCommentId();
        entity.postId = request.getPostId();
        entity.userId = request.getUserId();
        entity.content = request.getContent();
        entity.deleted = false;
        entity.createdAt = LocalDateTime.now();
        entity.updatedAt = LocalDateTime.now();
        return entity;
    }

    public Comment toModel() {
        return Comment.builder()
                .commentId(commentId)
                .parentCommentId(parentCommentId)
                .postId(postId)
                .userId(userId)
                .content(content)
                .deleted(deleted)
                .createdAt(createdAt)
                .build();
    }

    public void assignSelfAsParentIfRoot(Comment parent) {
        if (parent == null) {
            this.parentCommentId = this.commentId;
        }
    }
}
