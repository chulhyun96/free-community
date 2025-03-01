package com.cheolhyeon.free_community.post.controller.response;

import com.cheolhyeon.free_community.post.domain.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponse {
    private String categoryName;
    private Long userId;
    private LocalDateTime createdAt;
    private String title;
    private String content;
    private String images;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;

    public static PostCreateResponse from(Post newPost) {
        PostCreateResponse response = new PostCreateResponse();
        return response;
    }
    // private List<Comment> comments = new ArrayList or HashSet -> 작성 날짜를 기반으로, 처음에는 empty
}
