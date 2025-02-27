package com.cheolhyeon.free_community.post.controller;

import com.cheolhyeon.free_community.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_community.post.service.PostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody PostCreateRequest request) {
        postService.create(request);
        return null;
    }

}
