package com.cheolhyeon.free_commnunity_1.comment.service;

import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;

    @Mock
    UserService userService;

    @Mock
    CommentEntity savedEntity;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("ParentComment 생성")
    void createCommentSuccessfully() {
        // Given
        Long userId = 1L;
        Long postId = 1L;

        CommentCreateRequest parentComment = CommentCreateRequest.builder()
                .parentCommentId(null)
                .userId(userId)
                .content("안녕하세요")
                .build();

        User mockUser = mock(User.class);
        CommentEntity savedEntity = mock(CommentEntity.class);
        Comment expectedComment = mock(Comment.class);

        given(userService.readById(userId)).willReturn(mockUser);
        given(commentRepository.save(any(CommentEntity.class))).willReturn(savedEntity);
        given(savedEntity.toModel()).willReturn(expectedComment);

        // When
        Comment comment = commentService.create(postId, parentComment);

        // Then
        assertThat(comment).isEqualTo(expectedComment);
        then(userService).should().readById(userId);
        then(commentRepository).should().save(any(CommentEntity.class));
        then(savedEntity).should().assignSelfAsParentIfRoot(null);
        then(savedEntity).should().toModel();
    }


    @Test
    @DisplayName("Comment Read")
    void readComment() {
        //given
        List<CommentEntity> comments = createCommentEntity();
        given(commentRepository.findByPostIdOrderByCreatedAtAsc(1L)).willReturn(comments);

        //when
        List<CommentReadResponse> result = commentService.read(1L);

        //then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCommentId()).isEqualTo(result.get(0).getReplies().get(0).getParentCommentId());
        assertThat(result.get(1).getCommentId()).isEqualTo(result.get(1).getReplies().get(0).getParentCommentId());
        assertThat(result.get(2).getCommentId()).isEqualTo(result.get(2).getReplies().get(0).getParentCommentId());
    }

    @Test
    @DisplayName("리스트가 비어있을 때")
    void getCommentsCountWithEmpty() {
        //given
        List<CommentReadResponse> comments = new ArrayList<>();
        //when
        int commentsCount = commentService.getCommentsCount(comments);
        //then
        assertThat(commentsCount).isZero();
    }

    @Test
    @DisplayName("댓글이 1개일때")
    void getCommentsCount_One() {
        //given
        List<CommentReadResponse> comments = new ArrayList<>();
        comments.add(CommentReadResponse.builder()
                .parentCommentId(1L)
                .commentId(1L)
                .content("안녕하세요")
                .build());
        //when
        int commentsCount = commentService.getCommentsCount(comments);
        //then
        assertThat(commentsCount).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글이 2개일때")
    void getCommentsCount_Two() {
        //given
        List<CommentReadResponse> comments = new ArrayList<>();
        comments.add(CommentReadResponse.builder()
                .parentCommentId(1L)
                .commentId(1L)
                .content("안녕하세요")
                .build());
        comments.add(CommentReadResponse.builder()
                .parentCommentId(1L)
                .commentId(2L)
                .content("안녕하세요")
                .build());
        //when
        int commentsCount = commentService.getCommentsCount(comments);
        //then
        assertThat(commentsCount).isEqualTo(2);
    }

    private List<CommentEntity> createCommentEntity() {
        List<CommentEntity> comments = new ArrayList<>();
        for (long i = 1L; i <= 3; i++) {
            CommentEntity rootComment = CommentEntity.builder()
                    .commentId(i)
                    .parentCommentId(i)
                    .postId(i)
                    .userId(i)
                    .content("Root Comment" + 1)
                    .deleted(false)
                    .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                    .updatedAt(LocalDateTime.of(2024, 1, 1, 1, 1, (int) (1 + i)))
                    .build();
            comments.add(rootComment);
        }
        for (long i = 1L; i <= 3; i++) {
            CommentEntity children = CommentEntity.builder()
                    .commentId(3 + i)
                    .parentCommentId(i)
                    .postId(i)
                    .userId(i)
                    .content("ChildrenComment" + 1)
                    .deleted(false)
                    .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                    .updatedAt(LocalDateTime.of(2024, 1, 1, 1, 1, (int) (1 + i)))
                    .build();
            comments.add(children);
        }
        return comments;
    }
}