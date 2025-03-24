package com.cheolhyeon.free_commnunity_1.comment.controller.reponse;

import lombok.Getter;

import java.util.List;

@Getter
public class CommentPageResponse {
    private List<CommentReadResponse> comments;
    private Long commentCount;

    public static CommentPageResponse of(List<CommentReadResponse> comments, Long commentCount) {
        CommentPageResponse response = new CommentPageResponse();
        response.comments = comments;
        response.commentCount = commentCount;
        return response;
    }
}
