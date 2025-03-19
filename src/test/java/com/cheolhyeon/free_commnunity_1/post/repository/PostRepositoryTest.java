package com.cheolhyeon.free_commnunity_1.post.repository;

import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;


    @Test
    @DisplayName("특정 User 작성한 Post 찾아오기")
    void findByIdAndUserId() {
        PostEntity postEntity = postRepository.findByIdAndUserId(4L, 4L).get();
        assertThat(postEntity).isNotNull();
        assertThat(postEntity.getId()).isEqualTo(4L);
    }

    @Test
    @DisplayName("오늘 날짜 생성된 List<PostEntity>를 불러온다")
    void findPostsByDate() {
        //given
        for (int i = 0; i < 10; i++) {
            PostEntity post = PostEntity.builder()
                    .title("안녕 제목 " + i)
                    .content("안녕 내용 " + i)
                    .createdAt(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }

        PostEntity post = PostEntity.builder()
                .title("안녕 제목 " + 11)
                .content("안녕 내용 " + 11)
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();
        postRepository.save(post);

        //when
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        List<PostEntity> postsByDateOfToday = postRepository.findPostsByDate(startOfDay, endOfDay);

        //then
        assertThat(postsByDateOfToday).hasSize(10);
    }
}