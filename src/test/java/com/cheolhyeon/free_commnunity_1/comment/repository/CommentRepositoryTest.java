package com.cheolhyeon.free_commnunity_1.comment.repository;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;


    @Test
    @DisplayName("Root 댓글일 insert 쿼리 발생 후 update 쿼리 발생함")
    void insertAndUpdateParentComment() {
        //given
        CommentCreateRequest request = new CommentCreateRequest(1L, 1L, "안녕하세요");
        CommentEntity entity = commentRepository.save(CommentEntity.from(request, null, 1L));
        entity.assignSelfAsParentIfRoot(null);
        //when
        CommentEntity findEntity = commentRepository.findById(entity.getParentCommentId()).orElseThrow();
        //then
        assertThat(findEntity.getCommentId()).isEqualTo(entity.getParentCommentId());
    }

    @Test
    @DisplayName("Post Id에 맞는 Comment 끌고오기 (오래된 순 정렬)")
    void findByPostIdOrderByCreatedAtAsc() {
        //given
        Long postId = 10L;

        CommentEntity comment1 = CommentEntity.builder()
                .parentCommentId(1L)
                .postId(postId)
                .content("첫 번째 루트 댓글")
                .createdAt(LocalDateTime.of(2024, 3, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 3, 1, 10, 0))
                .deleted(false)
                .build();

        CommentEntity comment2 = CommentEntity.builder()
                .parentCommentId(2L)
                .postId(postId)
                .content("두 번째 루트 댓글")
                .createdAt(LocalDateTime.of(2024, 3, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2024, 3, 1, 12, 0))
                .deleted(false)
                .build();

        CommentEntity comment3 = CommentEntity.builder()
                .parentCommentId(1L)
                .postId(postId)
                .content("첫 번째 루트 댓글의 하위 댓글")
                .createdAt(LocalDateTime.of(2024, 3, 1, 15, 0))
                .updatedAt(LocalDateTime.of(2024, 3, 1, 15, 0))
                .deleted(false)
                .build();

        // 저장
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        //when
        List<CommentEntity> result = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        //then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getParentCommentId()).isEqualTo(comment1.getParentCommentId());
        assertThat(result.get(1).getParentCommentId()).isEqualTo(comment2.getParentCommentId());
        assertThat(result.get(2).getParentCommentId()).isEqualTo(comment1.getParentCommentId());
        assertThat(result.get(0).getContent()).isEqualTo(comment1.getContent());
        assertThat(result.get(1).getContent()).isEqualTo(comment2.getContent());
        assertThat(result.get(2).getContent()).isEqualTo(comment3.getContent());
    }
}