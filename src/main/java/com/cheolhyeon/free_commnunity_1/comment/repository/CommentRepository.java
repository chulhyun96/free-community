package com.cheolhyeon.free_commnunity_1.comment.repository;

import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c WHERE c.postId = :postId ORDER BY c.parentCommentId, c.createdAt ASC")
    List<CommentEntity> findByPostIdOrderByCreatedAtAsc(Long postId);

    Optional<CommentEntity> findByPostIdAndCommentId(Long postId, Long commentId);

    @Query(
            value = "select count(*) from (" +
                    "select comment_id from comment " +
                    "where post_id = :postId and parent_comment_id = :parentCommentId " +
                    "limit :limit" +
                    ") as cci",
            nativeQuery = true
    )
    int countBy(@Param("postId") Long postId, @Param("parentCommentId") Long parentCommentId, @Param("limit") long limit);
}

