package com.cheolhyeon.free_commnunity_1.comment.repository.entity;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentUpdateRequest;
import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "comment")
@ToString
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

    public static CommentEntity from(CommentCreateRequest request, Comment parentComment, Long postId) {
        CommentEntity entity = new CommentEntity();
        entity.parentCommentId = parentComment == null ? 0L : parentComment.getParentCommentId();
        entity.postId = postId;
        entity.userId = request.getUserId();
        entity.content = request.getContent();
        entity.deleted = false;
        entity.createdAt = LocalDateTime.now();
        entity.updatedAt = LocalDateTime.now();
        return entity;
    }
    public static CommentEntity of(Comment comment) {
        return CommentEntity.builder()
                .parentCommentId(comment.getParentCommentId())
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .deleted(comment.getDeleted())
                .createdAt(comment.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
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
                .updatedAt(updatedAt)
                .build();
    }

    public void assignSelfAsParentIfRoot(Comment parent) {
        if (parent == null) {
            this.parentCommentId = this.commentId;
        }
    }

    public void update(CommentUpdateRequest request) {
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    public void reported() {
        this.deleted = true;
        this.content = "신고에 의해 삭제된 댓글입니다.";
    }
}
