package com.cheolhyeon.free_community.user.repository;

import com.cheolhyeon.free_community.user.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

}
