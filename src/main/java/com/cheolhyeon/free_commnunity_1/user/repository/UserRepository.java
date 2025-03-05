package com.cheolhyeon.free_commnunity_1.user.repository;

import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
