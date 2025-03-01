package com.cheolhyeon.free_community.post.controller;

import com.cheolhyeon.free_community.post.controller.response.PostCreateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void createNewPost() throws Exception {
        //given
        String api = "/posts";
        PostCreateRequestTest request = PostCreateRequestTest.builder()
                .writerId(1L)
                .categoryId(1L)
                .title("Post Title 1")
                .content("Post Content 1")
                .build();

    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class PostCreateRequestTest {
        private Long writerId;
        private Long categoryId;
        private String title;
        private String content;
    }

}