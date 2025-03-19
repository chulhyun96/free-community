package com.cheolhyeon.free_commnunity_1.user.domain;

import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_commnunity_1.user.type.ActionPoint;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("UserCreateRequest를 받아서 User를 생성한다.")
    void from() {
        //given
        UserCreateRequest request = new UserCreateRequest("새로운 유저");

        //when
        User user = User.from(request);

        //then
        assertThat(user.getNickname()).isEqualTo(request.getNickname());
        assertThat(user.getActionPoint()).isZero();
    }

    @Test
    @DisplayName("기존의 User가 UserUpdateRequest로 부터 닉네임이 변경된 \"업데이트 닉네임\" 을 받아 닉네임을 변경한다.")
    void update() {
        //given
        User user = User.builder()
                .nickname("기존 닉네임")
                .build();
        UserUpdateRequest updateUser = UserUpdateRequest.builder()
                .nickname("업데이트 닉네임")
                .build();
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);
        //when
        User update = user.update(updateUser, time);
        //then
        Assertions.assertThat(update.getNickname()).isEqualTo(updateUser.getNickname());
        Assertions.assertThat(update.getUpdatedAt()).isEqualTo(time);
    }
    @Test
    @DisplayName("게시글 생성 당 ActionPoint를 얻는다")
    void getActionPint() {
        //given
        User user = User.builder()
                .actionPoint(0L)
                .nickname("기존 닉네임")
                .build();
        //when
        user.allocateActionPoint(ActionPoint.POST);
        //then
        Assertions.assertThat(user.getActionPoint()).isEqualTo(1);
    }
}