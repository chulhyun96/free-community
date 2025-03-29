package com.cheolhyeon.free_commnunity_1.comment.service;

import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentUpdateRequest;
import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.commentlike.service.CommentLikeService;
import com.cheolhyeon.free_commnunity_1.report.service.ReportService;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;

    @Mock
    UserService userService;

    @Mock
    CommentLikeService commentLikeService;

    @Mock
    ReportService reportService;

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

        UserEntity mockUser = mock(UserEntity.class);
        Comment expectedComment = mock(Comment.class);

        given(userService.getUserEntity(userId)).willReturn(mockUser);
        given(commentRepository.save(any(CommentEntity.class))).willReturn(savedEntity);
        given(savedEntity.toModel()).willReturn(expectedComment);

        // When
        Comment comment = commentService.create(postId, parentComment);

        // Then
        assertThat(comment).isEqualTo(expectedComment);
        then(commentRepository).should().save(any(CommentEntity.class));
        then(savedEntity).should().assignSelfAsParentIfRoot(null);
        then(savedEntity).should().toModel();
    }


    @Test
    @DisplayName("Comment Read")
    void readCommentsComment() {
        //given
        List<CommentEntity> comments = createCommentEntities();
        given(commentRepository.findAll(anyLong(), anyLong(), anyLong())).willReturn(comments);

        //when
        List<CommentReadResponse> result = commentService.readComments(1L, 0L, 20L);

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
        Long commentsCount = commentService.getCommentsCount(1L);
        //then
        assertThat(commentsCount).isZero();
    }

    @Test
    @DisplayName("Post의 총 댓글 수를 계산한다. (부모 댓글 + 자식댓글)")
    void getCommentsCount() {
        //given
        List<CommentReadResponse> comments = createMockComments();

        //when
        Long commentsCount = commentService.getCommentsCount(1L);

        //then
        assertThat(commentsCount).isZero();
    }

    @Test
    @DisplayName("업데이트를 위한 Comment 엔티티를 찾아오고 Comment엔티티를 수정한다")
    void update() {
        //given
        CommentEntity mockComment = mock(CommentEntity.class);
        CommentUpdateRequest mockUpdateRequest = mock(CommentUpdateRequest.class);
        given(commentRepository.findByPostIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.of(mockComment));

        //when
        commentService.update(1L, 1L, mockUpdateRequest);

        //then
        then(commentRepository).should(times(1))
                .findByPostIdAndCommentId(anyLong(), anyLong());
        then(mockComment).should(times(1))
                .update(mockUpdateRequest);
    }
    @Test
    @DisplayName("루트 Comment가 자식 Comment를 가지고 있고, 자식 Comment가 존재한다면, 루트 Comment 삭제 플래그를 재설정하고 업데이트를 수행한다")
    void deleteWhenChildrenCommentExist() {
        //given
        CommentEntity mockEntity = mock(CommentEntity.class);
        Comment mockComment = mock(Comment.class);

        given(commentRepository.findByPostIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.of(mockEntity));
        given(mockEntity.toModel())
                .willReturn(mockComment);
        given(mockComment.getDeleted())
                .willReturn(false);
        given(commentRepository.countBy(anyLong(), anyLong(), eq(2L)))
                .willReturn(2);
        //when
        commentService.delete(1L, 1L);

        //then
        then(mockComment).should(times(1)).delete();
        then(commentRepository).should(times(1))
                .save(any(CommentEntity.class));
        then(mockComment).should(times(1)).delete();
    }
    @Test
    @DisplayName("루트 Comment가 자식 Comment를 갖고있지 않다면 DB에서 삭제한다")
    void deleteWhenRootCommentWithoutChildrenComment() {
        //given
        CommentEntity mockEntity = mock(CommentEntity.class);
        Comment mockComment = mock(Comment.class);

        given(commentRepository.findByPostIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.of(mockEntity));
        given(mockEntity.toModel()).willReturn(mockComment);
        given(mockComment.getDeleted()).willReturn(false);
        given(commentRepository.countBy(anyLong(), anyLong(), eq(2L)))
                .willReturn(1);

        ArgumentCaptor<CommentEntity> commentCaptor = ArgumentCaptor.forClass(CommentEntity.class);
        //when
        commentService.delete(1L, 1L);

        //then
        then(commentRepository).should(times(1)).delete(commentCaptor.capture());
        assertThat(commentRepository.findById(anyLong())).isEmpty();
    }

    private List<CommentReadResponse>  createMockComments() {
        List<CommentReadResponse> comments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            comments.add(CommentReadResponse.builder()
                    .content("안녕" + i)
                    .build());
        }
        return comments;
    }

    private List<CommentEntity> createCommentEntities() {
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