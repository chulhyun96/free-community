package com.cheolhyeon.free_community.user.service.port;


import com.cheolhyeon.free_community.user.domain.User;

public interface UserRepository  {
    User save(User from);
}
