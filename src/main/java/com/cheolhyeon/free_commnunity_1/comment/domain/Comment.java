package com.cheolhyeon.free_commnunity_1.comment.domain;

import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


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

    @Builder.Default
    private List<Comment> replies = new ArrayList<>();

    public static List<Comment> buildCommentsTree(List<CommentEntity> commentEntities) {
        /*return getCommentsOfTree(commentEntities, new ArrayList<>());*/
        return getCommentsOfTree2(commentEntities);
    }

    // O(n^2)
    private static List<Comment> getCommentsOfTree(List<CommentEntity> commentEntities, List<Comment> parent) {
        for (CommentEntity commentEntity : commentEntities) {
            Comment comment = commentEntity.toModel();

            if (!comment.isRoot()) {
                parent.add(comment);
                continue;
            }

            for (Comment rootComment : parent) {
                if (rootComment.getCommentId().equals(comment.getParentCommentId())) {
                    rootComment.getReplies().add(comment);
                }
            }
        }
        return parent;
    }

    // O(n)
    private static List<Comment> getCommentsOfTree2(List<CommentEntity> commentEntities) {
        // 1. CommentEntity -> Comment 변환
        List<Comment> comments = commentEntities.stream()
                .map(CommentEntity::toModel)
                .toList();

        Map<Long, Comment> commentMap = comments.stream()
                .collect(Collectors.toMap(Comment::getCommentId, comment -> comment));

        Map<Boolean, List<Comment>> partitioned = comments.stream()
                .collect(Collectors.partitioningBy(Comment::isRoot));

        List<Comment> rootComments = partitioned.get(true);
        List<Comment> childComments = partitioned.get(false);

        childComments.forEach(comment -> {
            Comment parent = commentMap.get(comment.getParentCommentId());
            if (parent != null) parent.getReplies().add(comment);
        });
        return rootComments;
    }

    public boolean isRoot() {
        return Objects.equals(parentCommentId, commentId);
    }

    public boolean delete() {
        return deleted = true;
    }
}
