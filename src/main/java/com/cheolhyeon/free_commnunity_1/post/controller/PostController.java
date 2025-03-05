package com.cheolhyeon.free_commnunity_1.post.controller;

import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostCreateResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostReadResponse;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.service.PostService;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<?> create(
            @RequestPart("file") List<MultipartFile> images,
            @RequestPart("post") PostCreateRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        Post post = postService.create(images, request, userId);
        return ResponseEntity.ok(PostCreateResponse.from(post));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> readById(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        Post post = postService.readById(postId, userId);
        Long currentViewCount = postService.getCurrentViewCount(postId);
        User user = postService.getUser(userId);
        Category category = postService.getCategory(post.getCategoryId());
        return ResponseEntity.ok(PostReadResponse.from(post, currentViewCount, user.getNickname(), category.getName()));
    }
}
