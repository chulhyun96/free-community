package com.cheolhyeon.free_commnunity_1.user.domain;

import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.user.controller.response.CommentHistoryResponse;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserHistoryResponse;

import java.util.List;

public class CommentHistory {
    private final List<CommentEntity> histories;

    private CommentHistory(List<CommentEntity> histories) {
        this.histories = histories;
    }

    public static CommentHistory from(List<CommentEntity> histories) {
        return new CommentHistory(histories);
    }

    public void addHistory(UserHistoryResponse userHistory) {
        histories.forEach(commentEntity -> {
            String postTitle = userHistory.getPostTitle(
                    commentEntity.getPostId()
            );
            userHistory.addCommentHistory(
                    CommentHistoryResponse.of(
                            commentEntity.getCommentId(),
                            postTitle,
                            commentEntity.getContent()));
        });
    }
}
