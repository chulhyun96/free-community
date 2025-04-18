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
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<Comment> replies = new ArrayList<>();

    public static List<Comment> buildCommentsTree(List<CommentEntity> commentEntities) {
        return getCommentsOfTree2(commentEntities);
    }

    private static List<Comment> getCommentsOfTree2(List<CommentEntity> commentEntities) {
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

    public void delete() {
        deleted = true;
        content = "사용자 요청에 의해 삭제된 댓글입니다.";
    }
}
