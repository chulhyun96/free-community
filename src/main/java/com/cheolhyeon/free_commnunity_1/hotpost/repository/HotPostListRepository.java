package com.cheolhyeon.free_commnunity_1.hotpost.repository;

import com.cheolhyeon.free_commnunity_1.hotpost.controller.request.HotPostResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class HotPostListRepository {
    private static final String HOT_POST_KEY = "hotPost:%s";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final StringRedisTemplate hotPostRedisTemplate;
    private final ObjectMapper mapper;


    public void updateTopHotPosts(List<HotPostResponse> allPosts) {
        String key = generateKey(LocalDateTime.now());
        hotPostRedisTemplate.opsForZSet().removeRange(key, 0, -1);
        for (HotPostResponse newSortedHotPost : allPosts) {
            try {
                String newPostAsJson = mapper.writeValueAsString(newSortedHotPost);
                hotPostRedisTemplate.opsForZSet().add(key, newPostAsJson, newSortedHotPost.getTotalScore());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    public List<HotPostResponse> getTopNCurrentHotPosts(long topN) {
        String key = generateKey(LocalDateTime.now());

        Set<String> hotPostsJsonSet = hotPostRedisTemplate.opsForZSet().reverseRange(key, 0, topN - 1);
        return convertFromSetToList(Objects.requireNonNull(hotPostsJsonSet));
    }

    private List<HotPostResponse> convertFromSetToList(Set<String> hotPostsJsonSet) {
        List<HotPostResponse> hotPostList = new ArrayList<>();
        for (String json : hotPostsJsonSet) {
            try {
                HotPostResponse post = mapper.readValue(json, HotPostResponse.class);
                hotPostList.add(post);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return hotPostList;
    }

    private String generateKey(LocalDateTime date) {
        return HOT_POST_KEY.formatted(date.format(FORMATTER));
    }
}
