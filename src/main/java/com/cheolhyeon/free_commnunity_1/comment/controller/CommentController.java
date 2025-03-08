package com.cheolhyeon.free_commnunity_1.comment.controller;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
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
    public ResponseEntity<HttpStatus> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request) {
        commentService.create(postId, request);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
