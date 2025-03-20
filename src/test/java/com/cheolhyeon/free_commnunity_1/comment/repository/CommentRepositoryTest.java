package com.cheolhyeon.free_commnunity_1.comment.repository;

import com.cheolhyeon.free_commnunity_1.comment.controller.request.CommentCreateRequest;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.common.domain.DateManager;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;


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
    void findTop20ByPostIdOrderByCreatedAtAsc() {
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
        List<CommentEntity> result = commentRepository.findTop20ByPostIdOrderByCreatedAtAsc(postId);
        for (CommentEntity commentEntity : result) {
            System.out.println("commentEntity = " + commentEntity);
        }

        //then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getParentCommentId()).isEqualTo(comment1.getParentCommentId());
        assertThat(result.get(1).getParentCommentId()).isEqualTo(comment3.getParentCommentId());
        assertThat(result.get(2).getParentCommentId()).isEqualTo(comment2.getParentCommentId());
    }

    @Test
    @DisplayName("특정 유저가 최근 1달 이내의 댓글 목록을 불러온다.")
    void findByUserIdAndDate() {
        //given
        DateManager dateManager = new DateManager(LocalDateTime.now());
        LocalDateTime endDate = dateManager.getLocalDateNow();
        LocalDateTime startDate = dateManager.getMinusMonthsAsLocalDate(1);
        //when
        List<CommentEntity> result = commentRepository.findByUserIdAndDate(5L, startDate, endDate);

        //then
        assertThat(result).isNotEmpty();
    }


    private CommentEntity createChildrenComment() {
        return CommentEntity.builder()
                .parentCommentId(1L)
                .postId(1L)
                .content("자식 댓글")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CommentEntity createRootComment() {
        return CommentEntity.builder()
                .parentCommentId(1L)
                .postId(1L)
                .content("루트 댓글")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private UserEntity createUser() {
        return UserEntity.builder()
                .nickname("테스트 닉네임")
                .build();
    }

    private PostEntity createPost(UserEntity user) {
        return PostEntity.builder()
                .title("테스트 게시글")
                .content("내용")
                .userId(user.getId())
                .build();
    }
}