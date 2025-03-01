package com.cheolhyeon.free_community.user.domain;

import com.cheolhyeon.free_community.post.domain.Post;
import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.controller.request.UserUpdateRequest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private Long id;
    private String nickname;
    private Long actionPoint;
    private MyHistory myHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User from(UserCreateRequest request) {
        User user = new User();
        user.nickname = request.getUsername();
        user.actionPoint = 0L;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        user.myHistory = new MyHistory();
        return user;
    }

    public void updateNickname(UserUpdateRequest request) {
        this.nickname = request.getNickname();
        this.updatedAt = LocalDateTime.now();
    }

    // 점수제를 도입해서 점수가 일정 이상일 경우 고정 닉네임 신청 가능 -> 올린 게시글 10개 + 댓글 30개 이상일 경우 신청 가능
    // 신청 가능할 경우 SSE 푸시 알림 지원
    public void increaseActionPoint() {
        this.actionPoint += 1;
    }
    public void addPost(Post post) {
        myHistory.addPost(post);
    }

    /*
    public void addComment(Comment comment) {
        myHistory.addComment(comment);
    }
    * */


    // 차단 기능, 비정상적인 활동에 대한 차단(다른 사용자들로 부터 신고를 N회 이상 당한 계정에 대한 조취)

}
