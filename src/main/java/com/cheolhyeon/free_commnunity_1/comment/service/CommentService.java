package com.cheolhyeon.free_commnunity_1.comment.service;

import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentUpdateRequest;
import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.commentlike.service.CommentLikeService;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import com.cheolhyeon.free_commnunity_1.user.type.ActionPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.function.Predicate.not;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentLikeService commentLikeService;

    public Comment create(Long postId, CommentCreateRequest request) {
        UserEntity user = userService.getUserEntity(request.getUserId());
        Comment parent = findParentComment(request);
        CommentEntity entity = commentRepository.save(
                CommentEntity.from(request, parent, postId)
        );
        entity.assignSelfAsParentIfRoot(parent);
        user.allocateActionPoint(ActionPoint.COMMENT);
        return entity.toModel();
    }

    private Comment findParentComment(CommentCreateRequest request) {
        Long parentCommentId = request.getParentCommentId();
        if (parentCommentId == null) {
            return null;
        }
        return commentRepository.findById(parentCommentId)
                .map(CommentEntity::toModel)
                .filter(not(Comment::getDeleted))
                .filter(Comment::isRoot)
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<CommentReadResponse> readOrderByCreateAt(Long postId) {
        List<CommentEntity> commentEntities = commentRepository.findTop20ByPostIdOrderByCreatedAtAsc(postId);
        Map<Long, Long> likeReaderBoard = getLikeReaderBoard(postId, commentEntities);
        List<Comment> commentsOfTree = Comment.buildCommentsTree(commentEntities);
        return CommentReadResponse.of(commentsOfTree, likeReaderBoard);
    }

    @Transactional(readOnly = true)
    public List<CommentReadResponse> readOrderByCommentLikes(Long postId) {
        List<CommentEntity> commentEntities = commentRepository.findTop20ByPostIdOrderByCreatedAtAsc(postId);
        Map<Long, Long> likeReaderBoard = getLikeReaderBoard(postId, commentEntities);
        List<Comment> commentsOfTree = Comment.buildCommentsTree(commentEntities);

        List<CommentReadResponse> responses = CommentReadResponse.of(commentsOfTree, likeReaderBoard);
        return CommentReadResponse.orderByLikesCountDesc(responses);
    }

    private Map<Long, Long> getLikeReaderBoard(Long postId, List<CommentEntity> commentEntities) {
        Map<Long, Long> likeReaderBoard = new HashMap<>();
        for (CommentEntity commentEntity : commentEntities) {
            Long currentCommentLikeCount = commentLikeService.getCurrentCommentLikeCount(postId, commentEntity.getCommentId());
            likeReaderBoard.put(commentEntity.getCommentId(), currentCommentLikeCount);
        }
        return likeReaderBoard;
    }

    public int getCommentsCount(List<CommentReadResponse> comments) {
        return comments.size() + comments.stream()
                .mapToInt(comment -> comment.getReplies().size())
                .sum();
    }

    public void update(Long postId, Long commentId, CommentUpdateRequest request) {
        CommentEntity findEntity = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        findEntity.update(request);
    }

    public void delete(Long postId, Long commentId) {
        commentRepository.findByPostIdAndCommentId(postId, commentId)
                .map(CommentEntity::toModel)
                .filter(not(Comment::getDeleted))
                .ifPresent(comment -> {
                    if (hasChildren(comment)) {
                        // 자식을 가진 루트 댓글일 경우 delete flag와 내용만 업데이트
                        comment.delete();
                        commentRepository.save(CommentEntity.of(comment));
                    } else {
                        // 자식이 없는 경우 -> 즉 루트 댓글이거나, 하위 댓글이거나
                        delete(comment);
                    }
                });
    }

    private boolean hasChildren(Comment comment) {
        return commentRepository.countBy(comment.getPostId(), comment.getCommentId(), 2L) == 2;
    }

    private void delete(Comment comment) {
        // 자식이 없는 루트 댓글일 경우 삭제
        commentRepository.delete(CommentEntity.of(comment));
        // 하위 댓글일 경우
        if (!comment.isRoot()) {
            commentRepository.findById(comment.getParentCommentId())
                    .map(CommentEntity::toModel)
                    .filter(Comment::getDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::delete);
        }
    }
}
