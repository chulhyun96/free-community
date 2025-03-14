package com.cheolhyeon.free_commnunity_1.commentlike.controller;

import com.cheolhyeon.free_commnunity_1.commentlike.service.CommentLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentLikeController.class)
class CommentLikeControllerTest {
    @MockitoBean
    private CommentLikeService commentLikeService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("같은 유저가 좋아요를 누르면 증가하고, 다시 누르면 취소된다.")
    void toggleLikeFunctionality() throws Exception {
        // given
        Long postId = 1L;
        Long commentId = 1L;
        Long userId = 1L;

        Map<Long, Boolean> likeStatus = new HashMap<>();
        likeStatus.put(userId, false); // 초기 상태: 좋아요 안 누름

        long currentLike = 100L;

        given(commentLikeService.toggleLike(anyLong(), anyLong(), anyLong()))
                .willAnswer(invocation -> {
                    boolean isLiked = likeStatus.get(userId);
                    likeStatus.put(userId, !isLiked); // 상태 반전
                    return likeStatus.get(userId) ? currentLike + 1 : currentLike; // 좋아요 개수 반환
                });

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/like", postId, commentId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("101"));

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/like", postId, commentId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/like", postId, commentId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("101"));
    }
}