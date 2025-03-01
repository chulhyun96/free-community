package com.cheolhyeon.free_community.user.controller.request;

import com.cheolhyeon.free_community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String nickname;

    public static UserUpdateRequest from(User user) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.nickname = user.getNickname();
        return request;
    }
}
