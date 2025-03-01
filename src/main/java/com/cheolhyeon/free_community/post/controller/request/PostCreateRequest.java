package com.cheolhyeon.free_community.post.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    private Long writerId;
    private Long categoryId;
    private String title;
    private String content;
}
