package com.cheolhyeon.free_commnunity_1.user.domain;

import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("")
    void from() {
        //given
        UserCreateRequest request = new UserCreateRequest("새로운 유저");

        //when
        User user = User.from(request);

        //then
        assertThat(user.getNickname()).isEqualTo(request.getNickname());
        assertThat(user.getActionPoint()).isEqualTo(0L);
    }

}