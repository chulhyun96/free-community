package com.cheolhyeon.free_community.user.controller;

import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.controller.response.UserCreateResponse;
import com.cheolhyeon.free_community.user.domain.User;
import com.cheolhyeon.free_community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserCreateResponse> create(@RequestBody UserCreateRequest request) {
        User newUser = userService.create(request);
        return ResponseEntity.ok(UserCreateResponse.from(newUser));
    }
}
