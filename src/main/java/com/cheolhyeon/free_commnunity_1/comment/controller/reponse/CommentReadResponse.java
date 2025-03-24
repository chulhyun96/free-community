package com.cheolhyeon.free_commnunity_1.comment.controller.reponse;

import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentReadResponse {
    private Long parentCommentId;
    private Long commentId;
    private String content;
    private Long likeCount;
    private LocalDateTime createdAt;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommentReadResponse> replies = new ArrayList<>();


    public static List<CommentReadResponse> of(List<Comment> commentsOfTree, Map<Long, Long> likeReaderBoard) {
        if (commentsOfTree == null || commentsOfTree.isEmpty()) {
            return new ArrayList<>();
        }

        List<CommentReadResponse> responseList = new ArrayList<>();
        for (Comment comment : commentsOfTree) {
            Long likeCount = likeReaderBoard.get(comment.getCommentId());
            responseList.add(createResponse(comment, likeCount, likeReaderBoard));
        }
        return responseList;
    }

    private static CommentReadResponse createResponse(Comment comment, Long likeCount, Map<Long, Long> likeReaderBoard) {
        return CommentReadResponse.builder()
                .commentId(comment.getCommentId())
                .parentCommentId(comment.getParentCommentId())
                .content(comment.getContent())
                .likeCount(likeCount)
                .createdAt(comment.getCreatedAt())
                .replies(of(comment.getReplies(), likeReaderBoard))
                .build();
    }

    public static List<CommentReadResponse> orderByLikesCountDesc(List<CommentReadResponse> responses) {
        return responses.stream()
                .sorted(Comparator.comparingLong(CommentReadResponse::getLikeCount).reversed())
                .toList();
    }

    public static CommentReadResponse from(CommentEntity commentEntity) {
        return CommentReadResponse.builder()
                .commentId(commentEntity.getCommentId())
                .parentCommentId(commentEntity.getParentCommentId())
                .createdAt(commentEntity.getCreatedAt())
                .content(commentEntity.getContent())
                .build();
    }
}
