package com.cheolhyeon.free_commnunity_1.comment.service;

import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentUpdateRequest;
import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.function.Predicate.not;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;

    public Comment create(Long postId, CommentCreateRequest request) {
        userService.readById(request.getUserId());
        Comment parent = findParentComment(request);
        CommentEntity entity = commentRepository.save(
                CommentEntity.from(request, parent, postId)
        );
        entity.assignSelfAsParentIfRoot(parent);
        return entity.toModel();
    }

    private Comment findParentComment(CommentCreateRequest request) {
        Long parentCommentId = request.getParentCommentId();
        if (parentCommentId == null) {
            return null; // 루트 댓글이므로 상위 댓글 없음
        }
        return commentRepository.findById(parentCommentId)
                .map(CommentEntity::toModel)
                .filter(not(Comment::getDeleted))
                .filter(Comment::isRoot)
                .orElseThrow();
    }

    public List<CommentReadResponse> read(Long postId) {
        List<CommentEntity> commentEntities = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        List<Comment> commentsOfTree = Comment.buildCommentsTree(commentEntities);
        return CommentReadResponse.from(commentsOfTree);
    }

    public int getCommentsCount(List<CommentReadResponse> comments) {
        return comments.size() + comments.stream()
                .mapToInt(comment -> comment.getReplies().size()).sum();
    }

    public void update(Long postId, Long commentId, CommentUpdateRequest request) {
        CommentEntity findEntity = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        findEntity.update(request);
    }
}
