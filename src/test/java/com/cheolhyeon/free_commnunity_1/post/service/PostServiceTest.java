package com.cheolhyeon.free_commnunity_1.post.service;

import com.cheolhyeon.free_commnunity_1.category.service.CategoryService;
import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostUpdateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostReadResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostSearchResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.search.PostSearchCondition;
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
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;
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

    @Mock
    PostQueryRepository postQueryRepository;

    @Mock
    CategoryService categoryService;

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
        User userWithOnlyId = createUserWithOnlyId(1L);
        given(userService.readById(anyLong())).willReturn(userWithOnlyId);
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
        assertThat(userWithOnlyId.getActionPoint()).isEqualTo(1);
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
        PostReadResponse response = PostReadResponse.from(readPost, 1L, "User", Category.GENERAL.getName(), null, 0L, 0);
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

    @Test
    @DisplayName("검색조건으로 페이지 네이션 처리를 하며 정렬 순서는 기본값으로 최신순이다")
    void searchPostByCondOrderByCreatedAtDesc() {
        //given
        PostSearchCondition condition = new PostSearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<PostSearchResponse> list = getPostSearchResponses(pageRequest);
        given(postQueryRepository.searchByCond(any(PostSearchCondition.class), any(Pageable.class), anyString()))
                .willReturn(list);

        //when
        Page<PostSearchResponse> response = postService.searchPostByCond(condition, pageRequest, "");

        //then
        assertThat(response).isNotNull();
        assertThat(response.getContent())
                .extracting(PostSearchResponse::getPostId,
                        PostSearchResponse::getTitle,
                        PostSearchResponse::getCategoryName,
                        PostSearchResponse::getViewCount
                )
                .containsExactly(
                        tuple(2L, "테스트 제목@", "LIFE", 0L),
                        tuple(1L, "테스트 제목1", "GENERAL", 0L)
                );
        assertThat(response.getContent())
                .extracting(PostSearchResponse::getCreatedAt)
                .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("검색조건으로 페이지 네이션 처리를 하며 정렬 순서로 조회수를 하면  조회수 순이다")
    void searchPostByCondOrderByViewCountDesc() {
        //given
        PostSearchCondition condition = new PostSearchCondition();

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<PostSearchResponse> list = getPostSearchResponsesOrderByViewCount(pageRequest);
        given(postQueryRepository.searchByCond(any(PostSearchCondition.class), any(Pageable.class), anyString()))
                .willReturn(list);

        //when
        Page<PostSearchResponse> response = postService.searchPostByCond(condition, pageRequest, "viewCount");

        //then
        assertThat(response).isNotNull();
        assertThat(response.getContent())
                .extracting(PostSearchResponse::getPostId,
                        PostSearchResponse::getTitle,
                        PostSearchResponse::getCategoryName,
                        PostSearchResponse::getViewCount
                )
                .containsExactly(
                        tuple(1L, "테스트 제목1", "GENERAL", 0L),
                        tuple(2L, "테스트 제목@", "LIFE", 0L)
                );
        assertThat(response.getContent())
                .extracting(PostSearchResponse::getViewCount)
                .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("검색조건으로 무한스크롤 처리를 하며 정렬 순서는 기본값으로 최신순이다")
    void searchPostByCondAsInfiniteOrderByCreatedAtDesc() {
        //given
        PostSearchCondition condition = new PostSearchCondition();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<PostSearchResponse> responses = Stream.of(
                        new PostSearchResponse(1L, "테스트 제목1", "GENERAL", 0L, LocalDateTime.now().withYear(2024).minusMonths(2)),
                        new PostSearchResponse(2L, "테스트 제목@", "LIFE", 10L, LocalDateTime.now().withYear(2024).minusMonths(1)))
                .sorted(Comparator.comparing(PostSearchResponse::getCreatedAt).reversed())
                .toList();
        Slice<PostSearchResponse> responseSlice = new SliceImpl<>(responses, pageRequest, false); // 마지막 페이지

        given(postQueryRepository.searchBySearchCondInfiniteScroll(
                any(PostSearchCondition.class), any(Pageable.class), anyString()))
                .willReturn(responseSlice);
        //when
        Slice<PostSearchResponse> slice = postService.searchPostByCondAsInfinite(condition, pageRequest, "");

        //then
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .extracting(PostSearchResponse::getPostId,
                        PostSearchResponse::getTitle,
                        PostSearchResponse::getCategoryName,
                        PostSearchResponse::getViewCount
                )
                .containsExactly(
                        tuple(2L, "테스트 제목@", "LIFE", 0L),
                        tuple(1L, "테스트 제목1", "GENERAL", 0L)
                );
        assertThat(slice.getContent())
                .extracting(PostSearchResponse::getCreatedAt)
                .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("검색조건으로 무한스크롤 처리를 하며 조회순으로 정렬한다")
    void searchPostByCondAsInfiniteOrderByViewCountDesc() {
        //given
        PostSearchCondition condition = new PostSearchCondition();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<PostSearchResponse> responses = Stream.of(
                        new PostSearchResponse(1L, "테스트 제목1", "GENERAL", 0L, LocalDateTime.now().withYear(2024).minusMonths(2)),
                        new PostSearchResponse(2L, "테스트 제목@", "LIFE", 10L, LocalDateTime.now().withYear(2024).minusMonths(1)))
                .sorted(Comparator.comparing(PostSearchResponse::getViewCount).reversed())
                .toList();
        Slice<PostSearchResponse> responseSlice = new SliceImpl<>(responses, pageRequest, false); // 마지막 페이지

        given(postQueryRepository.searchBySearchCondInfiniteScroll(
                any(PostSearchCondition.class), any(Pageable.class), anyString()))
                .willReturn(responseSlice);
        //when
        Slice<PostSearchResponse> slice = postService.searchPostByCondAsInfinite(condition, pageRequest, "");

        //then
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .extracting(PostSearchResponse::getPostId,
                        PostSearchResponse::getTitle,
                        PostSearchResponse::getCategoryName,
                        PostSearchResponse::getViewCount
                )
                .containsExactly(
                        tuple(2L, "테스트 제목@", "LIFE", 0L),
                        tuple(1L, "테스트 제목1", "GENERAL", 0L)
                );
        assertThat(slice.getContent())
                .extracting(PostSearchResponse::getViewCount)
                .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("특정 게시글 조회 시 해당 게시글의 현재 조회수를 가지고 온다")
    void getCurrentViewCount() {
        //given
        given(viewCountService.getCurrentViewCount(anyLong()))
                .willReturn(2L);
        //when
        Long currentViewCount = postService.getCurrentViewCount(1L);
        //then
        assertThat(currentViewCount).isEqualTo(2L);
    }

    @Test
    @DisplayName("특정 유저 조회 시 해당 유저의 정보를 가지고 온다")
    void getUser() {
        //given
        User user = User.builder()
                .id(1L)
                .actionPoint(100L)
                .nickname("안녕하세요")
                .build();
        given(userService.readById(anyLong()))
                .willReturn(user);
        //when
        User result = postService.getUser(1L);

        //then
        assertThat(result).isEqualTo(user);
        assertThat(result)
                .extracting(User::getId,
                        User::getActionPoint,
                        User::getNickname
                )
                .containsExactly(1L, 100L, "안녕하세요");
    }

    @Test
    @DisplayName("카테고리 ID로 특정 카테고리 조회 시 해당 Category객체가 반환된다")
    void getCategory() {
        //given
        given(categoryService.getCategory(anyLong()))
                .willReturn(Category.GENERAL);
        //when
        Category result = postService.getCategory(1L);
        //then
        assertThat(result)
                .extracting(Category::getId, Category::getName)
                .contains(1L, "자유게시판");
    }

    private Page<PostSearchResponse> getPostSearchResponses(PageRequest pageRequest) {
        List<PostSearchResponse> postSearchResponses = Stream.of(
                        new PostSearchResponse(1L, "테스트 제목1", "GENERAL", 0L, LocalDateTime.now().withYear(2024).minusMonths(2)),
                        new PostSearchResponse(2L, "테스트 제목@", "LIFE", 10L, LocalDateTime.now().withYear(2024).minusMonths(1)))
                .sorted(Comparator.comparing(PostSearchResponse::getCreatedAt).reversed())
                .toList();
        return new PageImpl<>(postSearchResponses, pageRequest, 2);
    }

    private Page<PostSearchResponse> getPostSearchResponsesOrderByViewCount(PageRequest pageRequest) {
        List<PostSearchResponse> postSearchResponses = Stream.of(
                        new PostSearchResponse(1L, "테스트 제목1", "GENERAL", 100L, LocalDateTime.now().withYear(2024).minusMonths(2)),
                        new PostSearchResponse(2L, "테스트 제목@", "LIFE", 10L, LocalDateTime.now().withYear(2024).minusMonths(1)))
                .sorted(Comparator.comparing(PostSearchResponse::getViewCount).reversed())
                .toList();
        return new PageImpl<>(postSearchResponses, pageRequest, 2);
    }

    private List<MultipartFile> createImagesOnlyTwo() {
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.jpg", "image/jpeg", "some content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.jpg", "image/jpeg", "some content".getBytes());
        return List.of(file1, file2);
    }

    private User createUserWithOnlyId(Long id) {
        return User.builder().id(id).actionPoint(0L).build();
    }
}