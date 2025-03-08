package com.cheolhyeon.free_commnunity_1.comment.controller;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentUpdateRequest;
import com.cheolhyeon.free_commnunity_1.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<HttpStatus> create(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request) {
        commentService.create(postId, request);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<HttpStatus> update(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request) {
        commentService.update(postId, commentId, request);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
