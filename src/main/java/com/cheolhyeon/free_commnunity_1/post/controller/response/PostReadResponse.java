package com.cheolhyeon.free_commnunity_1.post.controller.response;


import com.cheolhyeon.free_commnunity_1.comment.controller.reponse.CommentReadResponse;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostReadResponse {
    private String title;
    private String content;
    private String nickname;
    private String imageUrl;
    private String categoryName;
    private Long viewCount;
    private Long commentCount;
    private Long likeCount;
    private List<CommentReadResponse> comments;
    private LocalDateTime updatedAt;

    public static PostReadResponse from(Post post, Long currentViewCount, String writer, String categoryName, List<CommentReadResponse> comments, Long currentPostLikeCount, Long commentCount) {
        PostReadResponse response = new PostReadResponse();
        response.title = post.getTitle();
        response.content = post.getContent();
        response.nickname = writer;
        response.imageUrl = post.getImageUrl();
        response.categoryName = categoryName;
        response.viewCount = currentViewCount;
        response.updatedAt = post.getUpdatedAt();
        response.commentCount = commentCount;
        response.likeCount = currentPostLikeCount;
        response.comments = comments;
        return response;
    }
}
