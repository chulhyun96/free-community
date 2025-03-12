package com.cheolhyeon.free_commnunity_1.post.repository;

import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
       log.info("PostEntity: {}", postEntity);
   }
}