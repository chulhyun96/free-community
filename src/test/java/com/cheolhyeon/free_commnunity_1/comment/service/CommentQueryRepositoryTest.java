package com.cheolhyeon.free_commnunity_1.comment.service;

import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Comparator;

import static com.cheolhyeon.free_commnunity_1.comment.repository.entity.QCommentEntity.commentEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CommentQueryRepository.class)
class CommentQueryRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Spy
    private JPAQueryFactory query = new JPAQueryFactory(em);

    @Autowired
    CommentQueryRepository repository;

    @Test
    @DisplayName("댓글 페이징이 정상적으로 동작하는지 확인")
    void readAllCommentsAsPage() {
        Pageable pageable = PageRequest.of(0, 2); // 1페이지, 2개씩 조회

        // When
        Page<CommentEntity> response = repository.readAllCommentsAsPage(5L, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(6);
        assertThat(response.getTotalPages()).isEqualTo(3);

        assertThat(response.getContent())
                .extracting(CommentEntity::getCreatedAt)
                .isSortedAccordingTo(Comparator.naturalOrder());

        assertThat(response.getContent())
                .extracting(CommentEntity::getCommentId, CommentEntity::getContent)
                .containsExactly(
                        tuple(9L, "사용자 요청에 의해 삭제된 댓글입니다."),
                        tuple(11L, "안녕 하위 댓글이야")
                );
    }

    @Test
    @DisplayName("첫 번째 페이지에서는 count 쿼리가 발생하지 않는다.")
    void countQueryDoesNotExcecute() {
        //given
        Pageable pageable = PageRequest.of(0, 2);
        //when
        repository.readAllCommentsAsPage(5L, pageable);
        //then
        verify(query, never()).select(commentEntity.count());
    }

    @Test
    @DisplayName("✅ 마지막 페이지에서는 count 쿼리가 실행되지 않는다.")
    void countQueryDoesNotExecuteOnLastPage() {
        // Given
        Pageable pageable = PageRequest.of(2, 2);

        // When
        repository.readAllCommentsAsPage(5L, pageable);

        // Then
        verify(query, never()).select(commentEntity.count());
    }
}