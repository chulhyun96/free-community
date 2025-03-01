package com.cheolhyeon.free_community.user.service;

import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_community.user.domain.User;
import com.cheolhyeon.free_community.user.repository.entity.UserEntity;
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

    public User getById(Long id) {
        // 사용자 활동 이력 관리, 최근 작성한 게시글(제목과 생성날짜)
        // 게시글에 댓글이 있다면 게시글 제목 밑에 댓글 내용과 댓글 생성 날짜
        return userRepository.findById(id).toModel();
    }

    public User update(Long id, UserUpdateRequest request) {
        UserEntity userEntity = userRepository.findById(id);

        User user = userEntity.toModel();
        user.updateNickname(request);
        userEntity.syncWith(user);

        return user;
    }
}
