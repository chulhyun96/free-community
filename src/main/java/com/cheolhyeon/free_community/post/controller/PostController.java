package com.cheolhyeon.free_community.post.controller;

import com.cheolhyeon.free_community.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_community.post.domain.Post;
import com.cheolhyeon.free_community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/posts")
    public ResponseEntity<?> createPost(
            @RequestPart("post") PostCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> images) {
        Post newPost = postService.create(request, images);
        return ResponseEntity.ok(newPost);
    }
}
