package com.cheolhyeon.free_community.user.service;

import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.domain.User;
import com.cheolhyeon.free_community.user.service.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void createUser() {
        //given
        UserCreateRequest request = new UserCreateRequest("newUser");
        User user = User.from(request);

        given(userRepository.save(any(User.class))).willReturn(user);

        // when (테스트 동작 실행)
        User createdUser = userService.create(request);

        // then (검증)
        assertAll(
                () -> assertThat(createdUser).isNotNull(),
                () -> assertThat(createdUser.getNickname()).isEqualTo("newUser"),
                () -> assertThat(createdUser.getActionPoint()).isEqualTo(0L)
        );
    }

    @Test
    void getById() {
        //given
        User getUser = User.builder()
                .id(1L)
                .nickname("getUser")
                .build();
        given(userRepository.findById(anyLong())).willReturn(getUser);

        //when
        User findUser = userService.getById(getUser.getId());

        //then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getNickname()).isEqualTo("getUser");
    }
}