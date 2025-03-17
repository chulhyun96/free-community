package com.cheolhyeon.free_commnunity_1.postlike.controller;

import com.cheolhyeon.free_commnunity_1.postlike.service.PostLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostLikeController.class)
class PostLikeControllerTest {
    @MockitoBean
    PostLikeService postLikeService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("같은 유저가 특정 게시글에 대해서 좋아요를 누르면 증가하고, 다시 누르면 취소된다.")
    void toggleLikeFunctionality() throws Exception {
        //given
        Long postId = 1L;
        Long userId = 1L;
        HashMap<Long, Boolean> likeStatus = new HashMap<>();
        likeStatus.put(userId, false);

        long currentLikes = 0L;

        given(postLikeService.toggleLike(anyLong(), anyLong()))
                .willAnswer(invocation -> {
                    Boolean isLiked = likeStatus.get(userId);
                    likeStatus.put(userId, !isLiked);
                    return likeStatus.get(userId) ? currentLikes + 1 : currentLikes;
                });
        //when then
        mockMvc.perform(post("/posts/{postId}/likes", postId)
                        .header("X-User-ID", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(post("/posts/{postId}/likes", postId)
                        .header("X-User-ID", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        mockMvc.perform(post("/posts/{postId}/likes", postId)
                        .header("X-User-ID", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

    }
}