package com.cheolhyeon.free_commnunity_1.post.controller.response;

import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponse {
    private String title;
    private String content;
    private String imageUrl;

    public static PostCreateResponse from(Post post) {
        PostCreateResponse response = new PostCreateResponse();
        response.title = post.getTitle();
        response.content = post.getContent();
        response.imageUrl = post.getImageUrl();
        return response;
    }
}
