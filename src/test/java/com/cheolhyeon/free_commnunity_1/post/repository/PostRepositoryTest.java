package com.cheolhyeon.free_commnunity_1.post.repository;

import com.cheolhyeon.free_commnunity_1.common.domain.DateManager;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;


    
    @Test
    @DisplayName("특정 User가 작성한 List<PostEntity>를 userId로 검색해서 불러온다.")
    void findByUserIdAndDate() {
        //given
        DateManager dateManager = new DateManager(LocalDateTime.now());
        LocalDateTime startDate = dateManager.getMinusMonthsFromNow(1);
        LocalDateTime endDate = dateManager.getLocalDateNow();
        //when
        List<PostEntity> resulst = postRepository.findByUserIdAndDate(4L, endDate, startDate);
        //then
        assertThat(resulst).isEmpty();
    }
}