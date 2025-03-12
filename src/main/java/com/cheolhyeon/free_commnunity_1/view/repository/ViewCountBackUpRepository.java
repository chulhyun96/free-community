package com.cheolhyeon.free_commnunity_1.view.repository;

import com.cheolhyeon.free_commnunity_1.view.repository.entity.ViewCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewCountBackUpRepository extends JpaRepository<ViewCountEntity, Long> {
    @Query(
            value = "update post_view_count set view_count = :viewCount " +
                    "where post_id = :postId and view_count < :viewCount",
            nativeQuery = true
    )
    @Modifying(clearAutomatically = true)
    int updateViewCount(@Param("postId") Long postId, @Param("viewCount") Long viewCount);
}
