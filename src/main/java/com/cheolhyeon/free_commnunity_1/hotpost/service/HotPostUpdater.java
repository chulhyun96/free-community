package com.cheolhyeon.free_commnunity_1.hotpost.service;

import com.cheolhyeon.free_commnunity_1.hotpost.controller.request.HotPostResponse;
import com.cheolhyeon.free_commnunity_1.hotpost.repository.HotPostListRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.postlike.service.PostLikeService;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotPostUpdater {
    private static final long TOP_N = 10;

    private final HotPostListRepository hotPostListRepository;
    private final HotPostScoreCalculator hotPostScoreCalculator;
    private final ViewCountService viewCountService;
    private final PostLikeService postLikeService;
    private final PostRepository postRepository;

    public List<HotPostResponse> getTopNCurrentHotPosts(long limit) {
        return hotPostListRepository.getTopNCurrentHotPosts(limit);
    }

    //    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(fixedRate = 10000)
    public void updateHotPosts() {
        List<PostEntity> postsByDateOfToday = getNewPostsFromDB();
        List<HotPostResponse> newPosts = getNewPostsWithTotalScore(postsByDateOfToday);

        List<HotPostResponse> allPosts = getMixedPrePostsWith(newPosts, TOP_N);

        List<HotPostResponse> newSortedHotPosts = getNewSortedHotPosts(allPosts, TOP_N);

        hotPostListRepository.updateTopHotPosts(newSortedHotPosts);
    }

    private List<HotPostResponse> getMixedPrePostsWith(List<HotPostResponse> newHotPosts, long limit) {
        List<HotPostResponse> preHotPosts = hotPostListRepository.getTopNCurrentHotPosts(limit);
        List<HotPostResponse> allPosts = new ArrayList<>(preHotPosts);
        allPosts.addAll(newHotPosts);
        return allPosts;
    }

    private List<HotPostResponse> getNewSortedHotPosts(List<HotPostResponse> allPosts, long limit) {
        return allPosts.stream()
                .sorted(Comparator.comparing(HotPostResponse::getTotalScore).reversed())
                .limit(limit)
                .toList();
    }

    private List<HotPostResponse> getNewPostsWithTotalScore(List<PostEntity> newPosts) {
        List<HotPostResponse> newHotPosts = new ArrayList<>();
        for (PostEntity postEntity : newPosts) {
            Long currentViewCount = viewCountService.getCurrentViewCount(postEntity.getId());
            Long currentPostLikeCount = postLikeService.getCurrentPostLikeCount(postEntity.getId());
            Long totalScore = hotPostScoreCalculator.calculateScore(currentViewCount, currentPostLikeCount);

            HotPostResponse newPost = HotPostResponse.from(postEntity.getTitle(), currentViewCount, currentPostLikeCount);
            newPost.allocateTotalScore(totalScore);
            newHotPosts.add(newPost);
        }
        return newHotPosts;
    }

    private List<PostEntity> getNewPostsFromDB() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        return postRepository.findPostsByDate(startOfDay, endOfDay);
    }
}
