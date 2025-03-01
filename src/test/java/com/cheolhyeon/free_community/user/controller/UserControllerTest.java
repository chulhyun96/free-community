package com.cheolhyeon.free_community.user.controller;

import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.controller.response.UserCreateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private UserCreateResponse response;


    @BeforeEach
    void setUp() throws Exception {
        String api = "/users";
        String nickname = "NewUserA";
        UserCreateRequest newUserRequest = new UserCreateRequest(nickname);

        MvcResult result = mockMvc.perform(post(api)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isOk())
                .andReturn();
        response = mapper.readValue(result.getResponse().getContentAsString(), UserCreateResponse.class);
    }

    @Test
    void createNewUser() throws Exception {
        //given
        String api = "/users";
        String nickname = "NewUserB";
        UserCreateRequest newUserRequest = new UserCreateRequest(nickname);
        //when
        MvcResult result = mockMvc.perform(post(api)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        UserCreateResponse newUserResponse = mapper.readValue(contentAsString, UserCreateResponse.class);
        //then
        assertThat(newUserResponse.getNickname()).isEqualTo(nickname);
        assertThat(newUserResponse.getActionPoint()).isEqualTo(0L);
    }

    @Test
    void readById() throws Exception {
        String api = "/users/{id}";
        String responseNickname = response.getNickname();

        MvcResult result = mockMvc.perform(get(api, 1L))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        response = mapper.readValue(contentAsString, UserCreateResponse.class);

        assertThat(response.getNickname()).isEqualTo(responseNickname);
    }
}
