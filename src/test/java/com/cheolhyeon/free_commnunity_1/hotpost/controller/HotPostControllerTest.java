package com.cheolhyeon.free_commnunity_1.hotpost.controller;

import com.cheolhyeon.free_commnunity_1.hotpost.controller.request.HotPostResponse;
import com.cheolhyeon.free_commnunity_1.hotpost.service.HotPostUpdater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotPostController.class)
class HotPostControllerTest {

    @MockitoBean
    HotPostUpdater updater;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("상위 N개의 인기 게시글을 불러온다")
    void getHotPostTopN() throws Exception {
        List<HotPostResponse> sortedHotPosts = List.of(
                HotPostResponse.from("Post 1",
                        200L,
                        134L),
                HotPostResponse.from("Post 2",
                        100L,
                        100L)
        );
        BDDMockito.given(updater.getTopNCurrentHotPosts(10)).willReturn(sortedHotPosts);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/hot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Post 1")) // ✅ 가장 높은 점수
                .andExpect(jsonPath("$[1].title").value("Post 2")); // ✅ 두 번째 높은 점수*
    }
}