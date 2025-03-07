package com.cheolhyeon.free_commnunity_1.post.service;

import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostUpdateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostReadResponse;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.ImageStrategy;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;

    @Mock
    ImageStrategy imageStrategy;

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
    final String localImages1 = "[\"/Users/cheolhyeon/desktop/test1.jpg\",\"/Users/cheolhyeon/desktop/test2.jpg\"]";
    final String localImages2 = "[\"/Users/cheolhyeon/desktop/update1.jpg\",\"/Users/cheolhyeon/desktop/update2.jpg\"]";

    @BeforeEach
    void setUp() {
        request = new PostCreateRequest(1L, "제목", "내용");
        post = Post.from(request, userId, localImages1);
    }

    @Test
    @DisplayName("PostService Create 메서드 테스트")
    void create() throws JsonProcessingException {
        // Given
        given(userService.readById(anyLong())).willReturn(createUserWithOnlyId(1L));
        given(imageStrategy.formatToSave(anyList())).willReturn(localImages1);

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

    @Test
    @DisplayName("Post Update 기존의 이미지만 수정한 경우")
    void updatePostWithoutPostTitleAndContent() throws JsonProcessingException {
        //given
        MockMultipartFile updateFile1 = new MockMultipartFile("file", "update1.jpg", "image/jpeg", "update1.jpg".getBytes());
        MockMultipartFile updateFile2 = new MockMultipartFile("file", "update2.jpg", "image/jpeg", "update2.jpg".getBytes());
        List<MultipartFile> updateImagesList = List.of(updateFile1, updateFile2);
        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("")
                .content("")
                .build();

        given(postRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(PostEntity.from(post)));
        given(imageStrategy.formatToSave(any(), any(), anyString())).willReturn(localImages2);

        //when
        Post update = postService.update(updateImagesList, null, request, userId, postId);
        log.info("update: {}", update.toString());

        //then
        assertThat(update.getTitle()).isEqualTo(request.getTitle());
        assertThat(update.getContent()).isEqualTo(request.getContent());
        assertThat(update.getUserId()).isEqualTo(userId);
        assertThat(update.getImageUrl()).isEqualTo("[\"/Users/cheolhyeon/desktop/update1.jpg\",\"/Users/cheolhyeon/desktop/update2.jpg\"]");
    }

    @Test
    @DisplayName("Post Update 게시글만 수정한 경우")
    void updatePostWithoutImages() throws JsonProcessingException {
        //given
        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("수정합니다")
                .content("이미지의 변경없이 이미지는 그대로 두고 변경을 원합니다")
                .build();
        given(postRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(PostEntity.from(post)));
        given(imageStrategy.formatToSave(any(), any(), anyString())).willReturn(localImages2);

        //when
        Post update = postService.update(null, null, request, userId, postId);
        log.info("update: {}", update.toString());

        //then
        assertThat(update.getTitle()).isEqualTo(request.getTitle());
        assertThat(update.getContent()).isEqualTo(request.getContent());
        assertThat(update.getUserId()).isEqualTo(userId);
        assertThat(update.getImageUrl()).isEqualTo("[\"/Users/cheolhyeon/desktop/update1.jpg\",\"/Users/cheolhyeon/desktop/update2.jpg\"]");
    }

    @Test
    @DisplayName("기존 이미지가 있는 상태에서 새로운 이미지의 변경없이 null로 들어오는 경우")
    void testKeepExistingImage() throws JsonProcessingException {
        //given
        final String BASE_PATH = "/Users/cheolhyeon/desktop";

        given(postRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(PostEntity.from(post)));

        String existingImages = List.of(BASE_PATH + "/image1.jpg", BASE_PATH + "/image2.jpg", BASE_PATH + "/image3.jpg").toString();
        given(imageStrategy.formatToSave(any(), any(), anyString())).willReturn(existingImages);

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("")
                .content("")
                .build();
        //when
        Post updatePost = postService.update(null, null, updateRequest, userId, postId);
        log.info("update: {}", updatePost.toString());
        //then
        assertThat(updatePost).isNotNull();
        assertThat(updatePost.getImageUrl()).isEqualTo(existingImages);
    }

    @Test
    @DisplayName("기존 이미지가 없는 상태에서 새로운 이미지가 들어온 경우")
    void addNewImagesWithoutExistingImages() throws JsonProcessingException {
        //given
        final String BASE_PATH = "/Users/cheolhyeon/desktop";
        given(postRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(PostEntity.from(post)));


        List<MultipartFile> updateImagesList = getMockMultipartFileThree();
        String newImages = getExistingImages(BASE_PATH);

        given(imageStrategy.formatToSave(any(), any(), anyString())).willReturn(newImages);
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("")
                .content("")
                .build();
        //when
        Post updatePost = postService.update(updateImagesList, null, updateRequest, userId, postId);
        log.info("update: {}", updatePost.toString());
        //then
        assertThat(updatePost).isNotNull();
        assertThat(updatePost.getImageUrl()).isEqualTo(newImages);
    }

    @Test
    @DisplayName("기존 이미지가 있는 상태에서 새로운 이미지가 추가가 될 경우 ex) 기존 이미지 2장, 새로운 이미지 3장 + 총 5장이 저장되어야 하는 경우")
    void addNewImagesToExistingImages() throws JsonProcessingException {
        //given
        final String BASE_PATH = "/Users/cheolhyeon/desktop";
        given(postRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(PostEntity.from(post)));


        List<MultipartFile> updateImages = getMockMultipartFileThree();
        String result = List.of(
                        BASE_PATH + "/image1.jpg",
                        BASE_PATH + "/image2.jpg",
                        BASE_PATH + "/update1.jpg",
                        BASE_PATH + "/update2.jpg",
                        BASE_PATH + "/update3.jpg")
                .toString();
        given(imageStrategy.formatToSave(any(), any(), anyString())).willReturn(result);
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("")
                .content("")
                .build();
        //when
        Post update = postService.update(updateImages, null, updateRequest, userId, postId);
        log.info("update: {}", update.toString());
        //then
        assertThat(update).isNotNull();
        assertThat(update.getImageUrl()).isEqualTo(result);
    }

    @Test
    @DisplayName("사용자가 기존 이미지를 완전히 비운 상태에서 새로운 이미지를 추가한 경우")
    void deleteAllAndAddNewImages() throws JsonProcessingException {
        //given
        final String BASE_PATH = "/Users/cheolhyeon/desktop";
        given(postRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(PostEntity.from(post)));

        List<String> existingImages = List.of(BASE_PATH + "/image1.jpg", BASE_PATH + "/image1.jpg");
        List<MultipartFile> updateImages = getMockMultipartFileThree();
        String result = List.of(
                        BASE_PATH + "/update1.jpg",
                        BASE_PATH + "/update2.jpg",
                        BASE_PATH + "/update3.jpg")
                .toString();
        given(imageStrategy.formatToSave(any(), any(), anyString())).willReturn(result);
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("")
                .content("")
                .build();
        //when
        Post update = postService.update(updateImages, existingImages, updateRequest, userId, postId);
        log.info("update: {}", update.toString());
        //then
        assertThat(update).isNotNull();
        assertThat(update.getImageUrl()).isEqualTo(result);
    }

    @Test
    @DisplayName("사용자가 기존 2장의 이미지 중에서 1장을 삭제하고 새로운 1장을 추가한 경우")
    void deleteSomeAndAddNewImages() throws JsonProcessingException {
        final String BASE_PATH = "/Users/cheolhyeon/desktop";

        given(postRepository.findByIdAndUserId(anyLong(), anyLong()))
                .willReturn(Optional.of(PostEntity.from(post)));

        List<String> deletedImages = List.of(BASE_PATH + "/image1.jpg"); // 삭제할 이미지
        List<MultipartFile> updateImages = getMockMultipartFileThree(); // 새 이미지 3개

        String result = List.of(
                BASE_PATH + "/update1.jpg",
                BASE_PATH + "/image1.jpg"
        ).toString();

        given(imageStrategy.formatToSave(anyList(), anyList(), anyString()))
                .willReturn(result);

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("")
                .content("")
                .build();

        // when
        Post actualResult = postService.update(updateImages, deletedImages, updateRequest, userId, postId);

        // then
        log.info("Expected: {}", result);
        log.info("Actual: {}", actualResult);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getImageUrl()).isEqualTo(result);
    }

    private String getExistingImages(String BASE_PATH) {
        return List.of(BASE_PATH + "/image1.jpg", BASE_PATH + "/image1.jpg").toString();
    }

    private List<MultipartFile> getMockMultipartFileThree() {
        MockMultipartFile updateFile1 = new MockMultipartFile(
                "file",
                "update1.jpg",
                "image/jpeg",
                "update1.jpg".getBytes()
        );
        MockMultipartFile updateFile2 = new MockMultipartFile(
                "file",
                "update2.jpg",
                "image/jpeg",
                "update2.jpg".getBytes()
        );
        MockMultipartFile updateFile3 = new MockMultipartFile(
                "file",
                "update3.jpg",
                "image/jpeg",
                "update2.jpg".getBytes()
        );
        return List.of(updateFile1, updateFile2, updateFile3);
    }
    @Test
    @DisplayName("Post Delete")
    void deletePost() {
        //given
        given(postRepository.findByIdAndUserId(anyLong(), anyLong()))
                .willReturn(Optional.of(PostEntity.from(post)));

        ArgumentCaptor<PostEntity> postCaptor = ArgumentCaptor.forClass(PostEntity.class);

        //when
        postService.delete(postId, userId);

        //then
        verify(postRepository).delete(postCaptor.capture());
        PostEntity value = postCaptor.getValue();

        assertThat(value).isNotNull();
        assertThat(value.getUserId()).isEqualTo(userId);
        assertThat(value.getTitle()).isEqualTo(post.getTitle());
        assertThat(value.getContent()).isEqualTo(post.getContent());
        assertThat(value.getCategoryId()).isEqualTo(Category.GENERAL.getId());
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