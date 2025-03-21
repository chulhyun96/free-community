package com.cheolhyeon.free_commnunity_1.user.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@Getter
@NoArgsConstructor
public class UserHistoryResponse {
    private final Map<Long, PostHistoryResponse> postHistoryMap = new HashMap<>();
    private final Map<Long, CommentHistoryResponse> commentHistoryMap = new HashMap<>();


    public void addPostHistory(PostHistoryResponse history) {
        postHistoryMap.put(history.getPostId(), history);
    }

    public void addCommentHistory(CommentHistoryResponse history) {
        commentHistoryMap.put(history.getCommentId(), history);
    }

    public String getPostTitle(Long postId) {
        if (postHistoryMap.containsKey(postId)) {
            return postHistoryMap.get(postId).getTitle();
        }
        return "";
    }

}
