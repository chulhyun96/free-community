package com.cheolhyeon.free_commnunity_1.post.repository;

import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("select p from PostEntity p where p.id = :postId and p.userId = :userId")
    Optional<PostEntity> findByIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("select p from PostEntity p where p.createdAt >= :startOfDay and p.createdAt < :endOfDay")
    List<PostEntity> findPostsByDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
