package com.cheolhyeon.free_commnunity_1.user.service;

import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserHistoryResponse;
import com.cheolhyeon.free_commnunity_1.user.domain.CommentHistory;
import com.cheolhyeon.free_commnunity_1.user.domain.PostHistory;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.repository.UserRepository;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ViewCountService viewCountService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("UserCreate 테스트")
    void create() {
        //given
        UserCreateRequest newRequest = new UserCreateRequest();
        User newUser = User.from(newRequest);
        UserEntity newEntity = UserEntity.from(newUser);

        given(userRepository.save(any(UserEntity.class))).willReturn(newEntity);
        //when
        User createUser = userService.create(newRequest);

        //then
        assertThat(createUser).isNotNull();
    }

    @Test
    @DisplayName("유저 단일 조회 테스트")
    void readById() {
        //given
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .actionPoint(0L)
                .nickname("nickname")
                .build();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(entity));

        //when
        User user = userService.readById(1L);

        //then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(entity.getId());
        assertThat(user.getActionPoint()).isEqualTo(entity.getActionPoint());
        assertThat(user.getNickname()).isEqualTo(entity.getNickname());
    }

    @Test
    @DisplayName("유저 업데이트")
    void update() {
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest("updatedNickname");

        UserEntity entity = mock(UserEntity.class);
        User oldUser = mock(User.class);
        User updatedUser = mock(User.class);

        LocalDateTime now = LocalDateTime.now();
        given(userRepository.findById(userId)).willReturn(Optional.of(entity));
        given(entity.toModel()).willReturn(oldUser);
        given(oldUser.update(request, now)).willReturn(updatedUser);

        // when
        User result = userService.updateById(userId, request, now);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedUser.getId());
        assertThat(result.getNickname()).isEqualTo(updatedUser.getNickname());

        verify(userRepository, times(1)).findById(userId);
        verify(entity, times(1)).toModel();
        verify(entity, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("getHistory 호출 시 각각의 Entity의 Repository에서 쿼리가 발생한다")
    void getHistoryOccurQuery() {
        // given
        Long userId = 1L;
        given(postRepository.findByUserIdAndDate(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());
        given(commentRepository.findByUserIdAndDate(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());

        // when
        userService.getHistory(userId);

        // then:
        then(postRepository).should().findByUserIdAndDate(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class));
        then(commentRepository).should().findByUserIdAndDate(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("게시글과 댓글 이력이 모두 없는 경우 아무 작업도 수행하지 않는다.")
    void getHistoryNoReturn() {
        //given

        given(postRepository.findByUserIdAndDate(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(Collections.emptyList());

        given(commentRepository.findByUserIdAndDate(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(Collections.emptyList());

        try (MockedStatic<PostHistory> postHistoryStatic = mockStatic(PostHistory.class);
             MockedStatic<CommentHistory> commentHistoryStatic = mockStatic(CommentHistory.class)) {
            //when
            userService.getHistory(1L);

            //then
            postHistoryStatic.verifyNoInteractions();
            commentHistoryStatic.verifyNoInteractions();
            then(postRepository).should(times(1))
                    .findByUserIdAndDate(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
            then(commentRepository).should(times(1))
                    .findByUserIdAndDate(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Test
    @DisplayName("게시글 이력만 존재하는 경우 PostHistory.from과 addHistory가 호출된다.")
    void getHistoryOnlyPost() {
        //given
        PostEntity dummyPost = PostEntity.builder().build();
        List<PostEntity> postList = List.of(dummyPost);

        given(postRepository.findByUserIdAndDate(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(postList);
        given(commentRepository.findByUserIdAndDate(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(Collections.emptyList());

        PostHistory postHistory = mock(PostHistory.class);
        try (MockedStatic<PostHistory> postHistoryMockStatic = mockStatic(PostHistory.class)) {
            postHistoryMockStatic.when(() -> PostHistory.from(postList)).thenReturn(postHistory);

            //when
            userService.getHistory(1L);

            //then
            postHistoryMockStatic.verify(() -> PostHistory.from(postList), times(1));
            then(postHistory).should(times(1)).addHistory(any(UserHistoryResponse.class), eq(viewCountService));
            then(commentRepository).should().findByUserIdAndDate(
                    anyLong(),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            );
        }
    }

    @Test
    @DisplayName("댓글 이력만 존재하는 경우 CommentHistory.from과 addHistory가 호출되어야 한다.")
    void getHistoryOnlyComment() {
        //given
        CommentEntity dummyComment = CommentEntity.builder().build();
        List<CommentEntity> dummyList = List.of(dummyComment);
        given(commentRepository.findByUserIdAndDate(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(dummyList);

        given(postRepository.findByUserIdAndDate(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(Collections.emptyList());

        CommentHistory mockCommentHistory = mock(CommentHistory.class);
        try (MockedStatic<CommentHistory> commentHistoryStatic = mockStatic(CommentHistory.class)) {
            commentHistoryStatic.when(() -> CommentHistory.from(dummyList))
                    .thenReturn(mockCommentHistory);
            //when
            userService.getHistory(1L);

            //then
            commentHistoryStatic.verify(() -> CommentHistory.from(dummyList), times(1));
            then(mockCommentHistory).should().addHistory(any(UserHistoryResponse.class));
            then(postRepository).should()
                    .findByUserIdAndDate(
                            anyLong(),
                            any(LocalDateTime.class),
                            any(LocalDateTime.class)
                    );
        }
    }

    @Test
    @DisplayName("댓글 이력과 게시글 이력이 모두 존재할 경우 addHistory 메서드 모두 호출된다")
    void getHistoryCommentAndPost() {
        //given
        CommentEntity dummyComment = CommentEntity.builder().build();
        List<CommentEntity> dummyCommentList = List.of(dummyComment);
        given(commentRepository.findByUserIdAndDate(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(dummyCommentList);

        PostEntity dummyPost = PostEntity.builder().build();
        List<PostEntity> dummyPostList = List.of(dummyPost);
        given(postRepository.findByUserIdAndDate(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).willReturn(dummyPostList);

        try (MockedStatic<CommentHistory> commentHistoryStatic = mockStatic(CommentHistory.class);
             MockedStatic<PostHistory> postHistoryStatic = mockStatic(PostHistory.class)) {


            CommentHistory mockCommentHistory = mock(CommentHistory.class);
            commentHistoryStatic.when(() -> CommentHistory.from(dummyCommentList))
                    .thenReturn(mockCommentHistory);

            PostHistory mockPostHistory = mock(PostHistory.class);
            postHistoryStatic.when(() -> PostHistory.from(dummyPostList))
                    .thenReturn(mockPostHistory);

            //when
            userService.getHistory(1L);

            //then
            commentHistoryStatic.verify(() -> CommentHistory.from(dummyCommentList), times(1));
            postHistoryStatic.verify(() -> PostHistory.from(dummyPostList), times(1));
            then(mockCommentHistory).should().addHistory(any(UserHistoryResponse.class));
            then(mockPostHistory).should().addHistory(any(UserHistoryResponse.class), eq(viewCountService));
        }
    }
}
