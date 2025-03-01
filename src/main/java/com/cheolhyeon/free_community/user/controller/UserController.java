package com.cheolhyeon.free_community.user.controller;

import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_community.user.controller.response.UserCreateResponse;
import com.cheolhyeon.free_community.user.controller.response.UserDetailResponse;
import com.cheolhyeon.free_community.user.domain.User;
import com.cheolhyeon.free_community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserCreateResponse> create(@RequestBody UserCreateRequest request) {
        User newUser = userService.create(request);
        return ResponseEntity.ok(UserCreateResponse.from(newUser));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetailResponse> readById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(UserDetailResponse.from(user));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        User update = userService.update(id, request);
        return ResponseEntity.ok(UserDetailResponse.from(update));
    }
}
