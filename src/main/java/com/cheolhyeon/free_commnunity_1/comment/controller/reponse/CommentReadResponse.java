package com.cheolhyeon.free_commnunity_1.comment.controller.reponse;

import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentReadResponse {
    private Long parentCommentId;
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommentReadResponse> replies = new ArrayList<>();


    public static List<CommentReadResponse> from(List<Comment> commentsOfTree) {
        if (commentsOfTree == null || commentsOfTree.isEmpty()) {
            return new ArrayList<>();
        }

        List<CommentReadResponse> responseList = new ArrayList<>();
        for (Comment comment : commentsOfTree) {
            responseList.add(create(comment)); // create() 메서드 활용
        }
        return responseList;
    }


    private static CommentReadResponse create(Comment comment) {
        return CommentReadResponse.builder()
                .commentId(comment.getCommentId())
                .parentCommentId(comment.getParentCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .replies(from(comment.getReplies())) // 대댓글이 존재하면 재귀적으로 변환
                .build();
    }
}
