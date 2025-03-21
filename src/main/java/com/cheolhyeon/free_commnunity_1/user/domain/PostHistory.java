package com.cheolhyeon.free_commnunity_1.user.domain;

import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.user.controller.response.PostHistoryResponse;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserHistoryResponse;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;

import java.util.List;

public class PostHistory {
    private final List<PostEntity> histories;

    private PostHistory(List<PostEntity> histories) {
        this.histories = histories;
    }

    public static PostHistory from(List<PostEntity> histories) {
        return new PostHistory(histories);
    }

    public void addHistory(UserHistoryResponse userHistory, ViewCountService viewCountService) {
        histories.forEach(postEntity -> {
            Long currentViewCount = viewCountService.getCurrentViewCount(postEntity.getId());
            userHistory.addPostHistory(
                    PostHistoryResponse.of(
                            postEntity.getId(),
                            postEntity.getTitle(),
                            currentViewCount));
        });
    }
}
