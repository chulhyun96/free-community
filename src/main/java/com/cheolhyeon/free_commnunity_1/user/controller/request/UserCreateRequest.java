package com.cheolhyeon.free_commnunity_1.user.controller.request;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    private String nickname;
}
