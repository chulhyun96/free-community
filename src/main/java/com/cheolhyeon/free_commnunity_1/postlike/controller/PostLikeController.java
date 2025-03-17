package com.cheolhyeon.free_commnunity_1.postlike.controller;

import com.cheolhyeon.free_commnunity_1.postlike.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<Long> togglePostLike(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(postLikeService.toggleLike(postId, userId));
    }
}
