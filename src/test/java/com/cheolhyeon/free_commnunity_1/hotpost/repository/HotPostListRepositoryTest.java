package com.cheolhyeon.free_commnunity_1.hotpost.repository;

import com.cheolhyeon.free_commnunity_1.hotpost.controller.request.HotPostResponse;
import com.cheolhyeon.free_commnunity_1.hotpost.service.HotPostScoreCalculator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HotPostListRepositoryTest {

    @Mock
    private StringRedisTemplate template;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Mock
    private HotPostScoreCalculator hotPostScoreCalculator;

    @InjectMocks
    private HotPostListRepository hotPostListRepository;


    @Test
    @DisplayName("상위 N개의 게시글을 점수기준으로 내림차순으로 가져온다.")
    void getCurrentTopNHotPosts_ShouldReturnSortedPosts() throws JsonProcessingException {
        // given
        long topN = 10;
        String key = "hotPost:2025.03.18";

        List<String> sortedHotPostsJsonList = List.of(
                "{\"title\":\"Post 2\",\"currentViewCount\":200,\"currentPostLikeCount\":134}",
                "{\"title\":\"Post 1\",\"currentViewCount\":100,\"currentPostLikeCount\":100}"
        );

        Set<String> sortedHotPostsJsonSet = new LinkedHashSet<>(sortedHotPostsJsonList);

        given(template.opsForZSet()).willReturn(zSetOperations);
        given(zSetOperations.reverseRange(eq(key), eq(0L), eq(topN - 1)))
                .willReturn(sortedHotPostsJsonSet);

        given(mapper.readValue(anyString(), eq(HotPostResponse.class)))
                .willAnswer(invocation -> {
                    String json = invocation.getArgument(0);
                    if (json.contains("\"title\":\"Post 1\"")) {
                        return HotPostResponse.from("Post 1", 100L, 100L); // 점수: 300
                    } else {
                        return HotPostResponse.from("Post 2", 200L, 134L); // 점수: 468
                    }
                });

        // when
        List<HotPostResponse> topNCurrentHotPosts = hotPostListRepository.getTopNCurrentHotPosts(topN);

        // then
        assertThat(topNCurrentHotPosts).hasSize(2);
        assertThat(topNCurrentHotPosts.get(0).getTitle()).isEqualTo("Post 2");
        assertThat(topNCurrentHotPosts.get(1).getTitle()).isEqualTo("Post 1");
    }
}