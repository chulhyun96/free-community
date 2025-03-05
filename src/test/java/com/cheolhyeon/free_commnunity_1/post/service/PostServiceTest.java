package com.cheolhyeon.free_commnunity_1.post.service;

import com.cheolhyeon.free_commnunity_1.category.service.CategoryService;
import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostReadResponse;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.ImageStrategy;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.LocalImageFormatter;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;

    @Mock
    ImageStrategy imageStrategy;

    @Mock
    CategoryService categoryService;

    @Mock
    ViewCountService viewCountService;

    @Mock
    UserService userService;

    @InjectMocks
    PostService postService;

    Post post;
    PostCreateRequest request;

    final Long userId = 1L;
    final Long postId = 1L;
    final String localImages = "[\"/Users/cheolhyeon/desktop/test1.jpg\",\"/Users/cheolhyeon/desktop/test2.jpg\"]";

    @BeforeEach
    void setUp() {
        request = new PostCreateRequest(1L, "제목", "내용");
        post = Post.from(request, userId, localImages);
    }
    @Test
    @DisplayName("PostService Create 메서드 테스트")
    void create() {
        // Given
        given(userService.readById(anyLong())).willReturn(createUserWithOnlyId(1L));
        given(imageStrategy.formatToSave(anyList())).willReturn(localImages);

        ArgumentCaptor<PostEntity> postEntityCaptor = ArgumentCaptor.forClass(PostEntity.class);
        given(postRepository.save(postEntityCaptor.capture())).willReturn(PostEntity.from(post));

        // When
        postService.create(createImagesOnlyTwo(), request, userId);

        // Then
        PostEntity savedEntity = postEntityCaptor.getValue();
        assertThat(savedEntity.getTitle()).isEqualTo("제목");
        assertThat(savedEntity.getContent()).isEqualTo("내용");
        assertThat(savedEntity.getUserId()).isEqualTo(userId);
        then(userService).should().readById(anyLong());
        then(postRepository).should().save(any());
    }

    @Test
    @DisplayName("PostService readById 메서드 테스트")
    void readById() {
        //given
        given(postRepository.findById(anyLong())).willReturn(Optional.of(PostEntity.from(post)));
        given(viewCountService.increase(postId, userId)).willReturn(1L);
        //when
        Post readPost = postService.readById(postId, userId);
        PostReadResponse response = PostReadResponse.from(readPost, 1L, "User", Category.GENERAL.getName());
        //then

        assertThat(readPost.getTitle()).isEqualTo(response.getTitle());
        assertThat(readPost.getContent()).isEqualTo(response.getContent());
        assertThat(readPost.getUserId()).isEqualTo(userId);
        assertThat(readPost.getImageUrl()).isEqualTo(response.getImageUrl());
        assertThat(response.getViewCount()).isEqualTo(1L);
    }

    private List<MultipartFile> createImagesOnlyTwo() {
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.jpg", "image/jpeg", "some content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.jpg", "image/jpeg", "some content".getBytes());
        return List.of(file1, file2);
    }

    private User createUserWithOnlyId(Long id) {
        return User.builder().id(id).build();
    }
}