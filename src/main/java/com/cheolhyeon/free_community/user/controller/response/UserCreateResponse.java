package com.cheolhyeon.free_community.user.controller.response;

import com.cheolhyeon.free_community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponse {
    private Long id;
    private String nickname;
    private Long actionPoint;

    public static UserCreateResponse from(User newUser) {
        return new UserCreateResponse(newUser.getId(),newUser.getNickname(), newUser.getActionPoint());
    }
}
