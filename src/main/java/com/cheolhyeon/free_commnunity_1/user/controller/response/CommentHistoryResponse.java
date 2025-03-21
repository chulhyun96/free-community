package com.cheolhyeon.free_commnunity_1.user.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentHistoryResponse {
    @JsonIgnore
    private Long commentId;
    private String postTitle;
    private String content;

    public static CommentHistoryResponse of(Long commentId, String postTitle, String content) {
        CommentHistoryResponse commentHistoryResponse = new CommentHistoryResponse();
        commentHistoryResponse.commentId = commentId;
        commentHistoryResponse.postTitle = postTitle;
        commentHistoryResponse.content = content;
        return commentHistoryResponse;
    }
}
