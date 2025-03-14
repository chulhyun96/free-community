package com.cheolhyeon.free_commnunity_1.user.service;

import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.repository.UserRepository;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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
}