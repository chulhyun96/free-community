package com.cheolhyeon.free_commnunity_1.comment.service;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.domain.Comment;
import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.repository.UserRepository;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

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
        willDoNothing().given(savedEntity).initForRootComment();
        given(savedEntity.toModel()).willReturn(new Comment(1L, request.getContent(), 1L, 1L, 1L, false, LocalDateTime.now()));

        // when
        Comment comment = commentService.create(request);
        System.out.println("comment = " + comment);

        // then
        assertThat(comment.getParentCommentId()).isEqualTo(1L);
        assertThat(comment.getCommentId()).isEqualTo(1L);
        assertThat(comment.getParentCommentId()).isEqualTo(comment.getCommentId());
        then(savedEntity).should(times(1)).initForRootComment();
    }


}