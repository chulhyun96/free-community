package com.cheolhyeon.free_commnunity_1.postlike.service;

import com.cheolhyeon.free_commnunity_1.postlike.repository.PostLikeQueryRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PostLikeQueryRedisRepositoryTest {
    @Mock
    StringRedisTemplate template;

    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    PostLikeQueryRedisRepository repository;

    @Test
    @DisplayName("특정 게시글의 좋아요가 이미 눌렀을 경우 true를 반환한다.")
    void toggleLikeReturnTrue() {
        //given
        given(template.hasKey(anyString())).willReturn(true);
        //when
        boolean likedByUserIdAndPostId = repository.isLikedByUserIdAndPostId(1L, 1L);
        //then
        assertThat(likedByUserIdAndPostId).isTrue();
    }

    @Test
    @DisplayName("특정 게시글의 lock이 걸려있을 경우 false를 반환한다.")
    void lockReturnFalse() {
        //given
        given(template.hasKey(anyString())).willReturn(false);
        //when
        boolean likedByUserIdAndPostId = repository.isLikedByUserIdAndPostId(1L, 1L);
        //then
        assertThat(likedByUserIdAndPostId).isFalse();
    }
    @Test
    @DisplayName("특정 유저가 특정 게시글에 좋아요를 처음 눌렀을 경우 insert 메서드가 호출되며 key를 저장한다")
    void insert() {
        //given
        given(template.opsForValue()).willReturn(valueOperations);
        //when
        repository.insert(1L, 1L);
        //then
        then(valueOperations).should(times(1)).set(anyString(), anyString());
    }
    @Test
    @DisplayName("특정 유저가 특정 게시글에 좋아요를 또 누를 경우 delete 메서드가 호출되며 key를 삭제한다")
    void delete() {
        //given
        //when
        repository.delete(1L, 1L);
        //then
        then(template).should(times(1)).delete(anyString());
    }
}