package com.cheolhyeon.free_community.post.repository;

import com.cheolhyeon.free_community.post.domain.Post;
import com.cheolhyeon.free_community.post.repository.entity.PostEntity;
import com.cheolhyeon.free_community.post.service.port.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public PostEntity save(Post newPost) {
        return postJpaRepository.save(PostEntity.from(newPost));
    }
}
