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
    private static List<Comment> getCommentsOfTree(List<CommentEntity> commentEntities, List<Comment> root) {
        for (CommentEntity commentEntity : commentEntities) {
            Comment comment = commentEntity.toModel();

            if (comment.isRoot()) {
                root.add(comment);
                continue;
            }

            for (Comment rootComment : root) {
                if (rootComment.getCommentId().equals(comment.getParentCommentId())) {
                    rootComment.getReplies().add(comment);
                }
            }
        }
        return root;
    }

    // O(n)
    private static List<Comment> getCommentsOfTree2(List<CommentEntity> commentEntities) {
        // 1. CommentEntity -> Comment 변환
        List<Comment> comments = commentEntities.stream()
                .map(CommentEntity::toModel)
                .toList();

        // 2. 부모 ID를 키로 하고, 해당하는 자식 댓글 리스트를 값으로 저장하는 맵 생성 → O(n)
        Map<Long, Comment> commentMap = comments.stream()
                .collect(Collectors.toMap(Comment::getCommentId, comment -> comment));

        // ✅ 3. 부모 댓글 리스트 생성
        List<Comment> rootComments = new ArrayList<>();

        // ✅ 4. 부모-자식 관계 설정 (O(n))
        for (Comment comment : comments) {
            if (comment.isRoot()) {
                rootComments.add(comment);
            } else {
                // ✅ O(1)로 부모 댓글을 찾아서 대댓글 추가
                Comment parent = commentMap.get(comment.getParentCommentId());
                if (parent != null) {
                    parent.getReplies().add(comment);
                }
            }
        }
        return rootComments;
    }

    public boolean isRoot() {
        return Objects.equals(parentCommentId, commentId);
    }

    public boolean delete() {
        return deleted = true;
    }
}
