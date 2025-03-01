package com.cheolhyeon.free_community.user.domain;

import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.controller.request.UserUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void from() {
        UserCreateRequest test = new UserCreateRequest("test");
        User from = User.from(test);

        assertThat(from.getNickname()).isEqualTo(test.getUsername());
        assertThat(from.getActionPoint()).isEqualTo(0L);
    }

    @Test
    void updateNickname() {
        UserUpdateRequest updateTest = new UserUpdateRequest("update_test");

        User user = new User();
        user.updateNickname(updateTest);

        assertThat(user.getNickname()).isEqualTo(updateTest.getNickname());
    }

    @Test
    void increaseActionPoint() {
        UserCreateRequest test = new UserCreateRequest("test");
        User user = User.from(test);
        user.increaseActionPoint();
        assertThat(user.getActionPoint()).isEqualTo(1);
    }
}