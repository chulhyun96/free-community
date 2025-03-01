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
public class UserDetailResponse {
    private String nickname;

    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(user.getNickname());
    }
}
