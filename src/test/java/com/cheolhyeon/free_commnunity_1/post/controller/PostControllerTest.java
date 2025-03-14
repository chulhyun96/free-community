package com.cheolhyeon.free_commnunity_1.post.controller;

import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.comment.service.CommentService;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostUpdateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostCreateResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostReadResponse;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.ImageStrategy;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.LocalImageFormatter;
import com.cheolhyeon.free_commnunity_1.post.service.PostService;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockitoBean
    CommentService commentService;

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

    @Test
    @DisplayName("게시글 조회")
    void readByPostId() throws Exception {
        //given
        Long userId = 1L;
        Long postId = 1L;
        Post post = Post.builder()
                .id(1L)
                .categoryId(1L)
                .title("제목")
                .content("내용")
                .imageUrl("[]")
                .build();

        given(postService.readById(postId, userId)).willReturn(post);
        given(postService.getCurrentViewCount(postId)).willReturn(100L);

        User user = User.builder()
                .id(userId)
                .nickname("기존 유저")
                .build();

        given(postService.getUser(userId)).willReturn(user);
        given(postService.getCategory(1L)).willReturn(Category.GENERAL);

        List<CommentReadResponse> comments = createRootCommentWithReplies("RootContent", "ReplyContent");
        given(commentService.readOrderByCreateAt(postId)).willReturn(comments);

        given(commentService.getCommentsCount(comments)).willReturn(comments.size());
        //when
        MvcResult result = mockMvc.perform(get("/posts/{postId}/comments", postId)
                        .queryParam("sort", "latest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        PostReadResponse response = mapper.readValue(contentAsString, PostReadResponse.class);

        //then
        assertThat(response.getTitle()).isEqualTo(post.getTitle());
        assertThat(response.getContent()).isEqualTo(post.getContent());
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getImageUrl()).isEqualTo(post.getImageUrl());
        assertThat(response.getCategoryName()).isEqualTo(Category.GENERAL.getName());
        assertThat(response.getViewCount()).isEqualTo(100);
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getParentCommentId()).isEqualTo(1L);
        assertThat(comments.get(0).getReplies().get(0).getCommentId()).isEqualTo(2L);
    }


    @Test
    @DisplayName("Post Update")
    void updateById() throws JsonProcessingException {
        //given
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.jpg", "image/jpeg", "test".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.jpg", "image/jpeg", "test".getBytes());
        List<String> deletedImages = List.of();
        List<MultipartFile> images = List.of(file1, file2);
        String newImages = formatter.formatToSave(images);
        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("업데이트")
                .content("test")
                .build();

        given(postService.update(anyList(), anyList(), any(PostUpdateRequest.class), anyLong(), anyLong()))
                .willReturn(Post.builder().id(1L).title(request.getTitle()).content(request.getContent()).imageUrl(newImages).build());
        //when
        Post update = postService.update(images, deletedImages, request, 1L, 1L);
        System.out.println("update = " + update);
        //then
        assertThat(update).isNotNull();
        assertThat(update.getTitle()).isEqualTo(request.getTitle());
        assertThat(update.getContent()).isEqualTo(request.getContent());
        assertThat(update.getImageUrl()).isEqualTo(newImages);
    }


    private List<CommentReadResponse> createRootCommentWithReplies(String rootContent, String replycContent) {
        List<CommentReadResponse> response = new ArrayList<>();
        CommentReadResponse root = CommentReadResponse.builder()
                .commentId(1L)
                .parentCommentId(1L)
                .content(rootContent)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
                .replies(new ArrayList<>())
                .build();
        CommentReadResponse reply = CommentReadResponse.builder()
                .parentCommentId(1L)
                .commentId(2L)
                .content(replycContent)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 2))
                .replies(List.of())
                .build();
        root.getReplies().add(reply);
        response.add(root);
        return response;
    }
}