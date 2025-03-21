package com.cheolhyeon.free_commnunity_1.user.service;

import com.cheolhyeon.free_commnunity_1.comment.repository.CommentRepository;
import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.cheolhyeon.free_commnunity_1.common.domain.DateManager;
import com.cheolhyeon.free_commnunity_1.post.repository.PostRepository;
import com.cheolhyeon.free_commnunity_1.post.repository.entity.PostEntity;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_commnunity_1.user.controller.response.UserHistoryResponse;
import com.cheolhyeon.free_commnunity_1.user.domain.CommentHistory;
import com.cheolhyeon.free_commnunity_1.user.domain.PostHistory;
import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.repository.UserRepository;
import com.cheolhyeon.free_commnunity_1.user.repository.entity.UserEntity;
import com.cheolhyeon.free_commnunity_1.view.service.ViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ViewCountService viewCountService;

    public User create(UserCreateRequest request) {
        User user = User.from(request);
        UserEntity entity = UserEntity.from(user);
        return userRepository.save(entity).toModel();
    }

    @Transactional(readOnly = true)
    public User readById(Long userId) {
        UserEntity entity = userRepository.findById(userId).orElseThrow();
        return entity.toModel();
    }

    @Transactional(readOnly = true)
    public UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    public User updateById(Long userId, UserUpdateRequest request, LocalDateTime updatedAt) {
        UserEntity entity = userRepository.findById(userId).orElseThrow();
        User model = entity.toModel().update(request, updatedAt);
        entity.update(model);
        return model;
    }

    @Transactional(readOnly = true)
    public UserHistoryResponse getHistory(Long userId) {
        DateManager dateManager = new DateManager(LocalDateTime.now());
        LocalDateTime startDate = dateManager.getMinusMonthsFromNow(1);
        LocalDateTime endDate = dateManager.getLocalDateNow();

        UserHistoryResponse userHistory = new UserHistoryResponse();
        addPostHistory(userId, startDate, endDate, userHistory);
        addCommentHistory(userId, startDate, endDate, userHistory);
        return userHistory;
    }

    private void addPostHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate, UserHistoryResponse userHistory) {
        List<PostEntity> postsHistory = postRepository.findByUserIdAndDate(userId, startDate, endDate);
        if (postsHistory.isEmpty()) {
            return;
        }
        PostHistory history = PostHistory.from(postsHistory);
        history.addHistory(userHistory, viewCountService);
    }

    private void addCommentHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate, UserHistoryResponse userHistory) {
        List<CommentEntity> commentsHistory = commentRepository.findByUserIdAndDate(userId, startDate, endDate);
        if (commentsHistory.isEmpty()) {
            return;
        }
        CommentHistory history = CommentHistory.from(commentsHistory);
        history.addHistory(userHistory);
    }
}
