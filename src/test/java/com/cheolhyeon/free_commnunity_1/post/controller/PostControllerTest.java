package com.cheolhyeon.free_commnunity_1.post.controller;

import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.comment.service.CommentService;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostUpdateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostCreateResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostSearchResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.search.PostSearchCondition;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.ImageStrategy;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.LocalImageFormatter;
import com.cheolhyeon.free_commnunity_1.post.service.PostService;
import com.cheolhyeon.free_commnunity_1.postlike.service.PostLikeService;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @MockitoBean
    PostLikeService postLikeService;

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
        given(commentService.readComments(anyLong(), anyLong(), anyLong())).willReturn(comments);

        //when
        mockMvc.perform(get("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("sort", "createdAt")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                // 첫 번째 댓글 검증 (Root)
                .andExpect(jsonPath("$.comments[0].parentCommentId").value(1))
                .andExpect(jsonPath("$.comments[0].commentId").value(1))
                .andExpect(jsonPath("$.comments[0].content").value("RootContent"))
                .andExpect(jsonPath("$.comments[0].createdAt").value("2022-01-01T01:01:00"))
                //  첫 번째 댓글의 대댓글(Reply) 검증
                .andExpect(jsonPath("$.comments[0].replies[0].parentCommentId").value(1))
                .andExpect(jsonPath("$.comments[0].replies[0].commentId").value(2))
                .andExpect(jsonPath("$.comments[0].replies[0].content").value("ReplyContent"))
                .andExpect(jsonPath("$.comments[0].replies[0].createdAt").value("2024-01-01T01:02:00"))
                //  두 번째 댓글 검증
                .andExpect(jsonPath("$.comments[1].parentCommentId").value(3))
                .andExpect(jsonPath("$.comments[1].commentId").value(3))
                .andExpect(jsonPath("$.comments[1].content").value("뭘봐"))
                .andExpect(jsonPath("$.comments[1].createdAt").value("2024-01-01T01:01:00"))
                .andReturn();

    }

    @Test
    @DisplayName("게시글 상세 조회시 댓글의 좋아요 순 정렬")
    void readPostById_Likes() throws Exception {
        // given
        Long postId = 1L;
        Long userId = 123L;

        Post post = mock(Post.class);
        User user = mock(User.class);
        Category category = Category.GENERAL;

        List<CommentReadResponse> comments = List.of(
                new CommentReadResponse(2L, 2L, "좋아요가 많은 댓글", 15L, LocalDateTime.now(), List.of()),
                new CommentReadResponse(1L, 1L, "좋아요가 적은 댓글", 3L, LocalDateTime.now(), List.of())
        );

        given(postService.readById(postId, userId)).willReturn(post);
        given(postService.getCurrentViewCount(postId)).willReturn(100L);
        given(postService.getUser(userId)).willReturn(user);
        given(postService.getCategory(post.getCategoryId())).willReturn(category);

        mockMvc.perform(get("/posts/{postId}", postId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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

    @Test
    @DisplayName("전체 게시판을 불러오며 sorting을 따로 정하지 않으면 createdAt desc로 불러온다.")
    void readAll() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PostSearchResponse> postSearchResponses = Stream.of(
                        new PostSearchResponse(1L, "테스트 제목1", "GENERAL", 10L, LocalDateTime.now().withYear(2024).minusMonths(2)),
                        new PostSearchResponse(2L, "테스트 제목@", "LIFE", 20L, LocalDateTime.now().withYear(2024).minusMonths(1)))
                .sorted(Comparator.comparing(PostSearchResponse::getCreatedAt).reversed())
                .toList();
        Page<PostSearchResponse> list = new PageImpl<>(postSearchResponses, pageRequest, 2);

        given(postService.searchPostByCond(any(PostSearchCondition.class), any(Pageable.class), anyString()))
                .willReturn(list);

        //when
        mockMvc.perform(get("/posts")
                        .queryParam("sort", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("테스트 제목@"))
                .andExpect(jsonPath("$.content[1].title").value("테스트 제목1"));
    }

    @Test
    @DisplayName("전체 게시판을 불러오며 sorting을 viewCount로 하면 desc로 조회수가 가장 높은 순으로 불러온다.")
    void readAllSortedByViewCount() throws Exception {
        //given
        List<PostSearchResponse> postSearchResponses = Stream.of(
                        new PostSearchResponse(1L, "테스트 제목1", "GENERAL", 30L, LocalDateTime.now().withYear(2024).minusMonths(2)),
                        new PostSearchResponse(2L, "테스트 제목@", "LIFE", 20L, LocalDateTime.now().withYear(2024).minusMonths(1)))
                .toList();
        Page<PostSearchResponse> list = new PageImpl<>(postSearchResponses);
        given(postService.searchPostByCond(any(PostSearchCondition.class), any(Pageable.class), anyString()))
                .willReturn(list);

        //when
        mockMvc.perform(get("/posts")
                        .queryParam("sort", "viewCount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("테스트 제목1"))
                .andExpect(jsonPath("$.content[1].title").value("테스트 제목@"));
    }

    @Test
    @DisplayName("무한 스크롤 방식으로 게시글을 조회할 수 있다.")
    void readAllAsInfinity() throws Exception {
        // given (테스트용 응답 데이터 설정)
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<PostSearchResponse> responses = List.of(
                new PostSearchResponse(1L, "첫 번째 게시글", "GENERAL", 100L, LocalDateTime.now().minusDays(1)),
                new PostSearchResponse(2L, "두 번째 게시글", "LIFE", 50L, LocalDateTime.now().minusDays(2))
        );
        Slice<PostSearchResponse> responseSlice = new SliceImpl<>(responses, pageRequest, false); // 마지막 페이지
        given(postService.searchPostByCondAsInfinite(any(PostSearchCondition.class), any(Pageable.class), anyString()))
                .willReturn(responseSlice);

        //when  then
        mockMvc.perform(get("/posts-infinite")
                        .queryParam("sort", "createdAt")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("첫 번째 게시글"))
                .andExpect(jsonPath("$.content[1].title").value("두 번째 게시글"))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.last").value(true))
                .andDo(print());
    }


    private List<CommentReadResponse> createRootCommentWithReplies(String rootContent, String replycContent) {
        List<CommentReadResponse> response = new ArrayList<>();
        CommentReadResponse root1 = CommentReadResponse.builder()
                .commentId(1L)
                .parentCommentId(1L)
                .content(rootContent)
                .createdAt(LocalDateTime.of(2022, 1, 1, 1, 1))
                .replies(new ArrayList<>())
                .build();
        CommentReadResponse root2 = CommentReadResponse.builder()
                .commentId(3L)
                .parentCommentId(3L)
                .content("뭘봐")
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
        root1.getReplies().add(reply);
        response.add(root1);
        response.add(root2);
        return response;
    }
}