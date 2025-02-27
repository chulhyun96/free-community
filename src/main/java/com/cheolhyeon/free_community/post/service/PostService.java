package com.cheolhyeon.free_community.post.service;

import com.cheolhyeon.free_community.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_community.post.controller.response.PostCreateResponse;
import com.cheolhyeon.free_community.post.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostJpaRepository postJpaRepository;

    public PostCreateResponse create(PostCreateRequest request) {
        return null;
    }
}
