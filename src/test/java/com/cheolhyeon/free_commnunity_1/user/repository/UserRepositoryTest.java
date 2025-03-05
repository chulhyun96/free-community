package com.cheolhyeon.free_commnunity_1.user.repository;

import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 저장 및 조회")
    void saveAndFindUser() {
        // given
        UserEntity entity = UserEntity.builder()
                .nickname("testUser")
                .actionPoint(100L)
                .build();

        UserEntity savedEntity = userRepository.save(entity); // 저장

        // when
        Optional<UserEntity> foundEntity = userRepository.findById(savedEntity.getId());

        // then
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(foundEntity.get().getNickname()).isEqualTo(savedEntity.getNickname());
        assertThat(foundEntity.get().getActionPoint()).isEqualTo(savedEntity.getActionPoint());
    }

}