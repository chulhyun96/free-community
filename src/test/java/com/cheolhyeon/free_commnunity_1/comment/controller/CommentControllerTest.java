package com.cheolhyeon.free_commnunity_1.comment.controller;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentUpdateRequest;
import com.cheolhyeon.free_commnunity_1.comment.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @MockitoBean
    private CommentService commentService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;


    @Test
    @DisplayName("Create Comment")
    void create() throws Exception {
        //given
        CommentCreateRequest request =
                new CommentCreateRequest(null, 1L, "테스트 댓글 작성");

        //when
        mockMvc.perform(post("/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        // then
        verify(commentService, times(1)).create(eq(1L), any(CommentCreateRequest.class));
    }

    @Test
    @DisplayName("Update Comment")
    void doTest() throws Exception {
        //given
        CommentUpdateRequest request = CommentUpdateRequest.builder()
                .content("업데이트")
                .build();
        //when
        mockMvc.perform(patch("/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        //then
        verify(commentService, times(1)).update(eq(1L), eq(1L), any(CommentUpdateRequest.class));
    }

    @Test
    @DisplayName("Delete Comment")
    void deleteRootCommentWithoutChildren() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;

        // when & then
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isOk())
                .andDo(print());

        // verify
        verify(commentService, times(1)).delete(postId, commentId);
    }
}