package com.cheolhyeon.free_commnunity_1.commentlike.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CommentLikeQueryRedisRepositoryTest {

    @Mock
    StringRedisTemplate commentLikeRedisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    CommentLikeQueryRedisRepository commentLikeQueryRedisRepository;

    private final Long userId = 1L;
    private final Long commentId = 100L;
    private final String key = "comments_like:users:1:comments:100";

    @Test
    @DisplayName("특정 유저가 특정 댓글에 좋아요를 이미 눌렀다")
    void returnTrueWhenUserClickedLikeFirst() {
        //given
        given(commentLikeRedisTemplate.hasKey(key)).willReturn(true);
        //when
        boolean result =
                commentLikeQueryRedisRepository.isLikedByUserIdAndCommentId(userId, commentId);
        //then
        assertThat(result).isTrue();
        then(commentLikeRedisTemplate).should(times(1)).hasKey(key);
    }

    @Test
    @DisplayName("특정 유저가 특정 댓글에 좋아요를 처음 눌렀다")
    void returnFalseWhenUserClickedLikeSecond() {
        //given
        given(commentLikeRedisTemplate.hasKey(key)).willReturn(false);
        //when
        boolean result =
                commentLikeQueryRedisRepository.isLikedByUserIdAndCommentId(userId, commentId);
        //then
        assertThat(result).isFalse();
        then(commentLikeRedisTemplate).should(times(1)).hasKey(key);
    }

    @Test
    @DisplayName("insert 메서드 호출 시 Reids의 set이 호출되어 Key를 저장한다")
    void insert() {
        //given
        given(commentLikeRedisTemplate.opsForValue()).willReturn(valueOperations);
        //when
        commentLikeQueryRedisRepository.insert(userId, commentId);
        //then
        then(valueOperations).should(times(1)).set(key, "");
    }

    @Test
    @DisplayName("delete 메서드 호출 시 Reids의 delete이 호출되어 Key를 삭제한다")
    void delete() {
        //given
        //when
        commentLikeQueryRedisRepository.delete(userId, commentId);
        //then
        then(commentLikeRedisTemplate).should(times(1))
                .delete(key);
    }
}