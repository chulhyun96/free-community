package com.cheolhyeon.free_commnunity_1.comment.repository;

import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c WHERE c.postId = :postId ORDER BY c.parentCommentId, c.createdAt ASC limit 20")
    List<CommentEntity> findTop20ByPostIdOrderByCreatedAtAsc(Long postId);

    @Query(
            value = "select c.* " +
                    "from (" +
                    "   select comment_id " +
                    "   from comment " +
                    "   where post_id = :postId " +
                    "   limit :limit offset :offset " +
                    ") t " +
                    "left join comment c on t.comment_id = c.comment_id",
            nativeQuery = true
    )
    List<CommentEntity> findAll(
            @Param("postId") Long postId,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );

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

    @Query("select c from CommentEntity c where c.userId = :userId and c.createdAt between :startDate and :endDate")
    List<CommentEntity> findByUserIdAndDate(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("select count(*) from CommentEntity c where c.postId = :postId")
    Long count(@Param("postId") Long postId);
}

