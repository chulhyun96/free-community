package com.cheolhyeon.free_community.user.service.port;


import com.cheolhyeon.free_community.user.domain.User;
import com.cheolhyeon.free_community.user.repository.entity.UserEntity;

public interface UserRepository  {
    User save(User from);

    UserEntity findById(Long writerId);
}
