package com.cheolhyeon.free_commnunity_1.user.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateRequest {
    private String nickname;
}
