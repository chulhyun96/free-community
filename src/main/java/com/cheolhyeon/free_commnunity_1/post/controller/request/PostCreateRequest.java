package com.cheolhyeon.free_commnunity_1.post.controller.request;


import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    private Long categoryId;
    private String title;
    private String content;
}
