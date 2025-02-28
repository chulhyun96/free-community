package com.cheolhyeon.free_community.post.repository;

import com.cheolhyeon.free_community.post.repository.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

}
