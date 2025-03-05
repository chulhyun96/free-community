package com.cheolhyeon.free_commnunity_1.comment.repository;

import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}

