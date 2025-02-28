package com.cheolhyeon.free_community.user.repository;

import com.cheolhyeon.free_community.user.domain.User;
import com.cheolhyeon.free_community.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User request) {
        return userJpaRepository.save(UserEntity.from(request)).toModel();
    }
}
