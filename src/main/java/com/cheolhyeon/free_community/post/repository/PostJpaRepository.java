package com.cheolhyeon.free_community.post.repository;

import com.cheolhyeon.free_community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

}
