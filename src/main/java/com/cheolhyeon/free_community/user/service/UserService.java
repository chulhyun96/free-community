package com.cheolhyeon.free_community.user.service;

import com.cheolhyeon.free_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


}
