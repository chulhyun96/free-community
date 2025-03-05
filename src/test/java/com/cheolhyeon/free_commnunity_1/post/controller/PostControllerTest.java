package com.cheolhyeon.free_commnunity_1.post.controller;

import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostCreateResponse;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.ImageStrategy;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.LocalImageFormatter;
import com.cheolhyeon.free_commnunity_1.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    PostService postService;

    @Mock
    ImageStrategy formatter;

    @BeforeEach
    void setUp() {
        formatter = new LocalImageFormatter(mapper);
    }

    @Test
    @DisplayName("게시글 저장 with 이미지")
    void savePost() throws Exception {
        //given
        Long userId = 1L;
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.jpg", "image/jpeg", "test".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.jpg", "image/jpeg", "test".getBytes());
        List<MultipartFile> images = List.of(file1, file2);
        String image = formatter.formatToSave(images);

        PostCreateRequest request = new PostCreateRequest(1L, "제목", "내용");
        MockMultipartFile multipartFile = new MockMultipartFile("post", "", "application/json", mapper.writeValueAsBytes(request));

        Post post = Post.from(request, userId, image);
        given(postService.create(any(), any(), anyLong())).willReturn(post);

        //when
        MvcResult result = mockMvc.perform(multipart("/posts")
                        .file(file1)
                        .file(file2)
                        .file(multipartFile)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        PostCreateResponse response = mapper.readValue(contentAsString, PostCreateResponse.class);

        //then
        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.getContent()).isEqualTo("내용");
        assertThat(response.getImageUrl()).isEqualTo("[\"/Users/cheolhyeon/desktop/test1.jpg\",\"/Users/cheolhyeon/desktop/test2.jpg\"]");
    }
    @Test
    @DisplayName("게시글 저장 with No Image")
    void savePost_noImage() throws Exception {
        //given
        Long userId = 1L;
        PostCreateRequest request = new PostCreateRequest(1L, "제목", "내용");
        MockMultipartFile multipartFile1 = new MockMultipartFile("post", "", "application/json", mapper.writeValueAsBytes(request));
        MockMultipartFile emptyImage = new MockMultipartFile("file", "", "image/jpeg", "asdasd".getBytes());
        String image = formatter.formatToSave(List.of());

        Post post = Post.from(request, userId, image);
        given(postService.create(any(), any(), anyLong())).willReturn(post);
        //when
        MvcResult result = mockMvc.perform(multipart("/posts")
                        .file(multipartFile1)
                        .file(emptyImage)
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        PostCreateResponse response = mapper.readValue(result.getResponse().getContentAsString(), PostCreateResponse.class);
        //then
        assertThat(response.getImageUrl()).isEqualTo("[]");
    }
}