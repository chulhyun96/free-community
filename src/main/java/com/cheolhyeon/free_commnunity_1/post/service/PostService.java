package com.cheolhyeon.free_commnunity_1.post.service;

import com.cheolhyeon.free_commnunity_1.category.service.CategoryService;
import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.request.PostUpdateRequest;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostSearchResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.search.PostSearchCondition;
import com.cheolhyeon.free_commnunity_1.post.domain.Post;
import com.cheolhyeon.free_commnunity_1.post.image.formatter.ImageStrategy;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.report.service.ReportService;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import com.cheolhyeon.free_commnunity_1.user.service.UserService;
import com.cheolhyeon.free_commnunity_1.user.type.ActionPoint;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ImageStrategy imageStrategy;
    private final CategoryService categoryService;
    private final ViewCountService viewCountService;
    private final UserService userService;
    private final ReportService reportService;
    private final PostQueryRepository postQueryRepository;

    public Post create(List<MultipartFile> images, PostCreateRequest request, Long userId) throws JsonProcessingException {
        if (reportService.isBanned(userId)) {
            String banTTL = reportService.getBanTTL(userId);
            throw new EntityNotFoundException(
                    String.format("당신은 다른 사용자들로 부터 지속적인 신고로 인해 %s 동안 커뮤니티 활동이 금지 되었습니다.", banTTL)
            );
        }
        UserEntity user = userService.getUserEntity(userId);
        String jsonAsString = imageStrategy.formatToSave(images);
        Post post = Post.from(request, user.getId(), jsonAsString);
        user.allocateActionPoint(ActionPoint.POST);

        return postRepository.save(PostEntity.from(post)).toModel();
    }

    @Transactional(readOnly = true)
    public Post readById(Long postId, Long userId) {
        PostEntity entity = postRepository.findById(postId).orElseThrow();
        viewCountService.increase(postId, userId);
        return entity.toModel();
    }

    public Post update(List<MultipartFile> newImages, List<String> deletedImages, PostUpdateRequest request, Long userId, Long postId) throws JsonProcessingException {
        PostEntity entity = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        String jsonAsString = imageStrategy.formatToSave(newImages, deletedImages, entity.getImageUrl());
        entity.update(jsonAsString, request);
        return entity.toModel();
    }

    public void delete(Long postId, Long userId) {
        PostEntity entity = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public Page<PostSearchResponse> searchPostByCond(PostSearchCondition condition, Pageable pageable, String sort) {
        Page<PostSearchResponse> postSearchResponses = postQueryRepository.searchByCond(condition, pageable, sort);
        allocateViewCountOfPost(postSearchResponses);
        return postSearchResponses;
    }

    @Transactional(readOnly = true)
    public Slice<PostSearchResponse> searchPostByCondAsInfinite(PostSearchCondition condition, Pageable pageable, String sort) {
        Slice<PostSearchResponse> sliceResponse = postQueryRepository.searchBySearchCondInfiniteScroll(condition, pageable, sort);
        allocateViewCountOfPost(sliceResponse);
        return sliceResponse;
    }

    private void allocateViewCountOfPost(Page<PostSearchResponse> postSearchResponses) {
        for (PostSearchResponse postSearchResponse : postSearchResponses) {
            postSearchResponse.allocateCurrentViewCount(viewCountService.getCurrentViewCount(postSearchResponse.getPostId()));
        }
    }

    private void allocateViewCountOfPost(Slice<PostSearchResponse> postSearchResponses) {
        for (PostSearchResponse postSearchResponse : postSearchResponses) {
            postSearchResponse.allocateCurrentViewCount(viewCountService.getCurrentViewCount(postSearchResponse.getPostId()));
        }
    }

    public Long getCurrentViewCount(Long postId) {
        return viewCountService.getCurrentViewCount(postId);
    }

    public User getUser(Long userId) {
        return userService.readById(userId);
    }

    public Category getCategory(Long categoryId) {
        return categoryService.getCategory(categoryId);
    }
}
