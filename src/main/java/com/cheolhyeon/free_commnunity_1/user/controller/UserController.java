package com.cheolhyeon.free_commnunity_1.user.controller;

import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserCreateResponse;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserHistoryResponse;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserReadResponse;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserCreateResponse> create(@RequestBody UserCreateRequest request) {
        User user = userService.create(request);
        return ResponseEntity.ok(UserCreateResponse.from(user));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserReadResponse> readById(@PathVariable Long userId) {
        User user = userService.readById(userId);
        return ResponseEntity.ok(UserReadResponse.from(user));
    }

    @GetMapping("/users/{userId}/history")
    public ResponseEntity<UserHistoryResponse> getHistory(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getHistory(userId));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<UserReadResponse> updateById(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        LocalDateTime updatedAt = LocalDateTime.now();
        User user = userService.updateById(userId, request, updatedAt);
        return ResponseEntity.ok(UserReadResponse.from(user));
    }
}
