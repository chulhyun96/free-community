package com.cheolhyeon.free_commnunity_1.comment.controller.request;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {
    private String content;

}
