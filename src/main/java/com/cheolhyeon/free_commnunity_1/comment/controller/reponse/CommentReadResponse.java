package com.cheolhyeon.free_commnunity_1.comment.controller.reponse;

import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReadResponse {
    private Long parentCommentId;
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;

    public static List<CommentReadResponse> from(List<CommentEntity> commentEntities) {
        List<CommentReadResponse> commentList = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntities) {
            CommentReadResponse commentReadResponse = new CommentReadResponse();
            commentReadResponse.parentCommentId = commentEntity.getParentCommentId();
            commentReadResponse.commentId = commentEntity.getCommentId();
            commentReadResponse.content = commentEntity.getContent();
            commentReadResponse.createdAt = commentEntity.getCreatedAt();
            commentList.add(commentReadResponse);
        }
        return commentList;
    }
}
