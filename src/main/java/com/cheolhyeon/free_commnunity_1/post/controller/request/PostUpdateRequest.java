package com.cheolhyeon.free_commnunity_1.post.controller.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostUpdateRequest {
    private Long postId;
    private String title;
    private String content;
}
