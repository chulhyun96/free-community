package com.cheolhyeon.free_commnunity_1.user.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class UserHistoryResponse {
    private final Map<Long, PostHistory> postHistoryMap = new HashMap<>();
    private final Map<Long, CommentHistory> commentHistoryMap = new HashMap<>();


    public void addPostHistory(PostHistory history) {
        postHistoryMap.put(history.getPostId(), history);
    }

    public void addCommentHistory(CommentHistory history) {
        commentHistoryMap.put(history.getCommentId(), history);
    }

    public String getPostTitle(Long postId) {
        if (postHistoryMap.containsKey(postId)) {
            return postHistoryMap.get(postId).getTitle();
        }
        return "";
    }

}
