package com.cheolhyeon.free_commnunity_1.user.service;

import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.repository.UserRepository;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(UserCreateRequest request) {
        User user = User.from(request);
        UserEntity entity = UserEntity.from(user);
        return userRepository.save(entity).toModel();
    }

    public User readById(Long userId) {
        UserEntity entity = userRepository.findById(userId).orElseThrow();
        return entity.toModel();
    }

    public User updateById(Long userId, UserUpdateRequest request, LocalDateTime updatedAt) {
        UserEntity entity = userRepository.findById(userId).orElseThrow();
        User model = entity.toModel().update(request, updatedAt);
        entity.update(model);
        return model;
    }
}
