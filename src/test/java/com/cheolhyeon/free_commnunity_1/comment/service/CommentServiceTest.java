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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;

    @Mock
    UserService userService;

    @Mock
    CommentEntity savedEntity;  // ✅ Mock 객체로 변경

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("루트 댓글일 경우 자신의 commentId가 parentCommentId로 할당 된다.")
    void ThisCommentIsRoot() {
        //given
        CommentCreateRequest request = new CommentCreateRequest(1L, null, 1L, "안녕하세요");

        given(userService.readById(anyLong())).willReturn(User.builder().id(1L).build());
        given(commentRepository.save(any(CommentEntity.class))).willReturn(savedEntity);
        willDoNothing().given(savedEntity).assignSelfAsParentIfRoot(null);
        given(savedEntity.toModel()).willReturn(new Comment(1L, request.getContent(), 1L, 1L, 1L, false, LocalDateTime.now(), null));

        // when
        Comment comment = commentService.create(request);

        // then
        assertThat(comment.getParentCommentId()).isEqualTo(1L);
        assertThat(comment.getCommentId()).isEqualTo(1L);
        assertThat(comment.getParentCommentId()).isEqualTo(comment.getCommentId());
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