package com.cheolhyeon.free_commnunity_1.hotpost.service;

import com.cheolhyeon.free_commnunity_1.hotpost.controller.request.HotPostResponse;
import com.cheolhyeon.free_commnunity_1.hotpost.repository.HotPostListRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.postlike.service.PostLikeService;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HotPostUpdaterTest {
    @Mock
    HotPostListRepository hotPostListRepository;

    @Mock
    HotPostScoreCalculator hotPostScoreCalculator;

    @Mock
    ViewCountService viewCountService;

    @Mock
    PostLikeService postLikeService;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    HotPostUpdater hotPostUpdater;


    @Test
    @DisplayName("새로운 게시글의 점수를 계산한다")
    void getNowPostsWithTotalScore() {
        //given
        List<PostEntity> newPost = List.of(createPostEntity(1L, "새로운 게시글1"), createPostEntity(2L, "새로운 게시글2"));
        given(postRepository.findPostsByDate(any(), any()))
                .willReturn(newPost);
        given(viewCountService.getCurrentViewCount(1L))
                .willReturn(10L);
        given(postLikeService.getCurrentPostLikeCount(1L))
                .willReturn(10L);
        given(hotPostScoreCalculator.calculateScore(10L, 10L))
                .willReturn(30L);
        //when
        hotPostUpdater.updateHotPosts();

        //then
        then(viewCountService).should(times(newPost.size())).getCurrentViewCount(anyLong());
        then(postLikeService).should(times(newPost.size())).getCurrentPostLikeCount(anyLong());
        then(hotPostScoreCalculator).should(times(newPost.size())).calculateScore(anyLong(), anyLong());
    }

    @Test
    @DisplayName("limit 개수 만큼 인기글을 불러온다")
    void getTopNCurrentHotPosts() {
        //given
        List<HotPostResponse> hotPosts = List.of(
                HotPostResponse.from("인기글 1", 10L, 10L),
                HotPostResponse.from("인기글 2", 20L, 30L),
                HotPostResponse.from("인기글 3", 30L, 40L));
        given(hotPostUpdater.getTopNCurrentHotPosts(anyLong()))
                .willReturn(hotPosts);
        //when
        List<HotPostResponse> topNCurrentHotPosts = hotPostUpdater.getTopNCurrentHotPosts(10L);
        //then
        assertThat(topNCurrentHotPosts).containsExactly(
                hotPosts.get(0),
                hotPosts.get(1),
                hotPosts.get(2)
        ).hasSize(3);
    }

    private PostEntity createPostEntity(Long id, String title) {
        return PostEntity.builder()
                .id(id)
                .title(title)
                .build();
    }
}