package com.cheolhyeon.free_commnunity_1.view.repository;

import com.cheolhyeon.free_commnunity_1.view.repository.entity.ViewCountEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("업데이트 쿼리 테스트")
    void doTest() {
        //given
        viewCountBackUpRepository.save(
                ViewCountEntity.init(1L, 0L)
        );
        em.flush();
        em.clear();
        //when
        int result1 = viewCountBackUpRepository.updateViewCount(1L, 100L);
        int result2 = viewCountBackUpRepository.updateViewCount(1L, 300L);
        int result3 = viewCountBackUpRepository.updateViewCount(1L, 200L);

        //then
        Assertions.assertThat(result1).isEqualTo(1);
        Assertions.assertThat(result2).isEqualTo(1);
        Assertions.assertThat(result3).isEqualTo(0);
    }
}