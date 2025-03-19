package com.cheolhyeon.free_commnunity_1.hotpost.controller;

import com.cheolhyeon.free_commnunity_1.hotpost.controller.request.HotPostResponse;
import com.cheolhyeon.free_commnunity_1.hotpost.service.HotPostUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HotPostController {
    private final HotPostUpdater updater;

    @GetMapping("/posts/hot")
    public ResponseEntity<List<HotPostResponse>> getHotPostsTopN() {
        return ResponseEntity.ok(updater.getTopNCurrentHotPosts(10));
    }
}
