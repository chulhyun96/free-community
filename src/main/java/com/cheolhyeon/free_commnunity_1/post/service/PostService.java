package com.cheolhyeon.free_commnunity_1.post.service;

import com.cheolhyeon.free_commnunity_1.category.service.CategoryService;
import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.ImageStrategy;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ImageStrategy imageStrategy;
    private final CategoryService categoryService;
    private final ViewCountService viewCountService;
    private final UserService userService;

    public Post create(List<MultipartFile> images, PostCreateRequest request, Long userId) {
        User user = userService.readById(userId);
        String jsonAsString = imageStrategy.formatToSave(images);
        Post post = Post.from(request, user.getId(), jsonAsString);
        return postRepository.save(PostEntity.from(post)).toModel();
    }

    public Post readById(Long postId, Long userId) {
        // 게시글 찾고
        PostEntity entity = postRepository.findById(postId).orElseThrow();
        viewCountService.increase(postId, userId);
        return entity.toModel();
    }
    public Long getCurrentViewCount(Long postId) {
        return viewCountService.getCurrentViewCount(postId);
    }
    public User getUser(Long userId) {
        return userService.readById(userId);
    }
    public Category getCategory(Long categoryId) {
        return categoryService.findById(categoryId);
    }
}
