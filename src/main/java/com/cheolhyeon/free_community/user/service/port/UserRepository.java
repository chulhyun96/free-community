package com.cheolhyeon.free_community.user.service.port;


import com.cheolhyeon.free_community.user.domain.User;
import org.springframework.data.jpa.repository.Modifying;

public interface UserRepository  {
    User save(User from);

    User findById(Long writerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    User update(User user);
}
