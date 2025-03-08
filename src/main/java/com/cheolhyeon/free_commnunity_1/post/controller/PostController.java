package com.cheolhyeon.free_commnunity_1.post.controller;

import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.comment.service.CommentService;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostUpdateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostCreateResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostReadResponse;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.service.PostService;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @PostMapping("/posts")
    public ResponseEntity<PostCreateResponse> create(
            @RequestPart("file") List<MultipartFile> images,
            @RequestPart("post") PostCreateRequest request,
            @RequestHeader("X-User-Id") Long userId) throws JsonProcessingException {

        Post post = postService.create(images, request, userId);

        return ResponseEntity.ok(PostCreateResponse.from(post));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostReadResponse> readById(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {

        Post post = postService.readById(postId, userId);
        Long currentViewCount = postService.getCurrentViewCount(postId);
        User user = postService.getUser(userId);
        Category category = postService.getCategory(post.getCategoryId());
        List<CommentReadResponse> comments = commentService.read(postId);

        return ResponseEntity.ok(PostReadResponse.from(
                post,
                currentViewCount,
                user.getNickname(),
                category.getName(),
                comments,
                commentService.getCommentsCount(comments)
        ));
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<HttpStatus> update(
            @RequestPart("newImages") List<MultipartFile> newImages,
            @RequestPart("deletedImages") List<String> deletedImages,
            @RequestPart("post") PostUpdateRequest request,
            @PathVariable("postId") Long postId,
            @RequestHeader("X-User-Id") Long userId) throws JsonProcessingException {

        postService.update(newImages, deletedImages, request, postId, userId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<HttpStatus> delete(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        postService.delete(postId, userId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
