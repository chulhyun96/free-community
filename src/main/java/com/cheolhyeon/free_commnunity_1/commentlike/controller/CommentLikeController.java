package com.cheolhyeon.free_commnunity_1.commentlike.controller;

import com.cheolhyeon.free_commnunity_1.commentlike.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @PostMapping("/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<Long> toggleCommentLike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(commentLikeService.toggleLike(userId, postId, commentId));
    }
}
