package com.cheolhyeon.free_commnunity_1.user.controller.response;

import com.cheolhyeon.free_commnunity_1.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReadResponse {
    private String nickname;
    //private MyHistory myHistory;
    private LocalDateTime createdAt;


    public static UserReadResponse from(User user) {
        UserReadResponse response = new UserReadResponse();
        response.nickname = user.getNickname();
        response.createdAt = user.getCreatedAt();
        return response;
    }
}
