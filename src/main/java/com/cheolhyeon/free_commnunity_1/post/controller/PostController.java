package com.cheolhyeon.free_commnunity_1.post.controller;

import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.comment.service.CommentService;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostUpdateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostCreateResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostReadResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostSearchResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.search.PostSearchCondition;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.service.PostService;
import com.cheolhyeon.free_commnunity_1.postlike.service.PostLikeService;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;

    @PostMapping("/posts")
    public ResponseEntity<PostCreateResponse> create(
            @RequestPart("file") List<MultipartFile> images,
            @RequestPart("post") PostCreateRequest request,
            @RequestHeader("X-User-Id") Long userId) throws JsonProcessingException {

        Post post = postService.create(images, request, userId);
        return ResponseEntity.ok(PostCreateResponse.from(post));
    }

    @GetMapping("/posts")
    public Page<PostSearchResponse> readAll(
            PostSearchCondition condition,
            Pageable pageable,
            @RequestParam(required = false, name = "sort") String sort) {
        return postService.searchPostByCond(condition, pageable, sort);
    }

    @GetMapping("/posts-infinite")
    public Slice<PostSearchResponse> readAllAsInfinity(
            PostSearchCondition condition,
            Pageable pageable,
            @RequestParam(required = false, name = "sort") String sort) {
        return postService.searchPostByCondAsInfinite(condition, pageable, sort);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostReadResponse> readById(
            @PathVariable("postId") Long postId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Long page,
            @RequestParam(name = "pageSize", defaultValue = "20", required = false) Long pageSize,
            @RequestHeader("X-User-Id") Long userId) {

        Post post = postService.readById(postId, userId);
        Long currentViewCount = postService.getCurrentViewCount(postId);
        Long currentPostLikeCount = postLikeService.getCurrentPostLikeCount(postId);
        User user = postService.getUser(userId);
        Category category = postService.getCategory(post.getCategoryId());
        List<CommentReadResponse> comments = commentService.readComments(postId, page, pageSize);

        return ResponseEntity.ok(PostReadResponse.from(
                post,
                currentViewCount,
                user.getNickname(),
                category.getName(),
                comments,
                currentPostLikeCount,
                commentService.getCommentsCount(postId)
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
