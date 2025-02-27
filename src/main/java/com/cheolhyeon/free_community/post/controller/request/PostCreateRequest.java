package com.cheolhyeon.free_community.post.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    private String categoryName;
    private String title;
    private String content;
//    private List<ImageConverter> images;
}
