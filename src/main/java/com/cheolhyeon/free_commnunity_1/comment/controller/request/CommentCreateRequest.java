package com.cheolhyeon.free_commnunity_1.comment.controller.request;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    private Long postId;
    private Long parentCommentId;
    private Long userId;
    private String content;
}
