package com.cheolhyeon.free_commnunity_1.user.controller;

import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserCreateResponse;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserReadResponse;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<?> create(@RequestBody UserCreateRequest request) {
        User user = userService.create(request);
        return ResponseEntity.ok(UserCreateResponse.from(user));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> readById(@PathVariable Long userId) {
        User user = userService.readById(userId);
        return ResponseEntity.ok(UserReadResponse.from(user));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> updateById(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        User user = userService.updateById(userId,request);
        return ResponseEntity.ok(UserReadResponse.from(user));
    }
}
