package com.cheolhyeon.free_community.post.service;

import com.cheolhyeon.free_community.post.controller.request.PostCreateRequest;
import com.cheolhyeon.free_community.post.domain.ImageSaveFormatter;
import com.cheolhyeon.free_community.post.domain.Post;
import com.cheolhyeon.free_community.post.service.port.PostRepository;
import com.cheolhyeon.free_community.user.domain.User;
import com.cheolhyeon.free_community.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageSaveFormatter saveFormatter;

    public Post create(PostCreateRequest request, List<MultipartFile> images) {
        Long writerId = getById(request).getId();
        String jsonFormToSave = saveFormatter.formatToSave(images);
        log.info("jsonFormToSave: {}", jsonFormToSave);
        return postRepository.save(
                Post.from(request, writerId, jsonFormToSave)
        ).toModel();
    }

    private User getById(PostCreateRequest request) {
        return userRepository.findById(request.getWriterId()).toModel();
    }
}
