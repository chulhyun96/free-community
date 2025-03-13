package com.cheolhyeon.free_commnunity_1.view.repository;

import com.cheolhyeon.free_commnunity_1.view.repository.entity.ViewCountEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ViewCountBackUpRepositoryTest {

    @Autowired
    ViewCountBackUpRepository viewCountBackUpRepository;

    @Test
    @DisplayName("업데이트가 성공하면 result의 값이 1이되고 실패하면 0이된다.")
    void update() {
        //given
        viewCountBackUpRepository.save(
                ViewCountEntity.init(1L, 0L)
        );

        //when
        int result1 = viewCountBackUpRepository.updateViewCount(1L, 100L);
        int result2 = viewCountBackUpRepository.updateViewCount(1L, 300L);
        int result3 = viewCountBackUpRepository.updateViewCount(1L, 200L);

        //then
        Assertions.assertThat(result1).isEqualTo(1);
        Assertions.assertThat(result2).isEqualTo(1);
        Assertions.assertThat(result3).isZero();
    }
}