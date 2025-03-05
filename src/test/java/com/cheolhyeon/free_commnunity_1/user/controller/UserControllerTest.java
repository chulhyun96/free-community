package com.cheolhyeon.free_commnunity_1.user.controller;

import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserCreateResponse;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    UserService userService;


    @Test
    @DisplayName("새로운 유저 생성")
    void create() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest("새로운 유저");
        User user = User.from(request);

        given(userService.create(any(UserCreateRequest.class))).willReturn(user);
        //when
        User createdUser = userService.create(request);

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        UserCreateResponse newResponse =
                mapper.readValue(result.getResponse().getContentAsString(), UserCreateResponse.class);
        // then
        assertThat(newResponse.getNickname()).isEqualTo(createdUser.getNickname());
    }
    @Test
    @DisplayName("유저 단일 조회")
    void readById() {
        //given
        final Long userId = 100L;
        User user = User.builder()
                .nickname("유저 단일 조회")
                .build();
        given(userService.readById(anyLong())).willReturn(user);
        //when
        User findedUser = userService.readById(userId);

        //then
        assertThat(findedUser).isEqualTo(user);
        assertThat(findedUser).isNotNull();
        assertThat(findedUser.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("유저 업데이트")
    void update() {
        //given
        final Long userId = 100L;
        UserUpdateRequest request = new UserUpdateRequest("업데이트 유저");
        User update = User.builder()
                .nickname(request.getNickname())
                .build();
        given(userService.updateById(anyLong(), any(UserUpdateRequest.class)))
                .willReturn(update);
        //when
        User user = userService.updateById(userId, request);

        //then
        assertThat(user).isEqualTo(update);
        assertThat(request.getNickname()).isEqualTo(user.getNickname());
    }

}