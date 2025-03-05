package com.cheolhyeon.free_commnunity_1.user.domain;

import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private Long id;
    private String nickname;
    private Long actionPoint;
//    private MyHistory myHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User from(UserCreateRequest request) {
        User user = new User();
        user.nickname = request.getNickname();
        user.actionPoint = 0L;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        return user;
    }

    public User update(UserUpdateRequest request) {
        this.nickname = request.getNickname();
        this.updatedAt = LocalDateTime.now();
        return this;
    }
}
