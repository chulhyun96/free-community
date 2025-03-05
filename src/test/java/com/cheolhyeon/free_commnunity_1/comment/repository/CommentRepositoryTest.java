package com.cheolhyeon.free_commnunity_1.comment.repository;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;


    @Test
    @DisplayName("Root 댓글일 insert 쿼리 발생 후 update 쿼리 발생함")
    void insertAndUpdateParentComment() {
        //given
        CommentCreateRequest request = new CommentCreateRequest(1L, null, 1L, "안녕하세요");
        CommentEntity entity = commentRepository.save(CommentEntity.from(request, null));
        entity.initForRootComment();
        //when
        CommentEntity findEntity = commentRepository.findById(entity.getParentCommentId()).orElseThrow();
        //then
        assertThat(findEntity.getCommentId()).isEqualTo(entity.getParentCommentId());
    }
}