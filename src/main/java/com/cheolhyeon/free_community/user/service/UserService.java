package com.cheolhyeon.free_community.user.service;

import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.domain.User;
import com.cheolhyeon.free_community.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(UserCreateRequest request) {
        return userRepository.save(User.from(request));
    }
}
