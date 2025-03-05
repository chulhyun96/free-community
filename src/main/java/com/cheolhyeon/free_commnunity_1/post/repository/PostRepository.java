package com.cheolhyeon.free_commnunity_1.post.repository;

import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
