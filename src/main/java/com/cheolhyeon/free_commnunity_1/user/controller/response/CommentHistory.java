package com.cheolhyeon.free_commnunity_1.user.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentHistory {
    @JsonIgnore
    private Long commentId;
    private String postTitle;
    private String content;

    public static CommentHistory of(Long commentId, String postTitle, String content) {
        CommentHistory commentHistory = new CommentHistory();
        commentHistory.commentId = commentId;
        commentHistory.postTitle = postTitle;
        commentHistory.content = content;
        return commentHistory;
    }
}
