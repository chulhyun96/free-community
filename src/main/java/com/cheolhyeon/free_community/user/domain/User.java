package com.cheolhyeon.free_community.user.domain;

import com.cheolhyeon.free_community.user.controller.request.UserCreateRequest;
import com.cheolhyeon.free_community.user.controller.request.UserUpdateRequest;
import com.cheolhyeon.free_community.user.repository.entity.UserEntity;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static User from(UserCreateRequest request) {
        User user = new User();
        user.nickname = request.getUsername();
        user.actionPoint = 0L;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
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
    // 사용자 활동 이력 관리(최근 작성한 게시글, 댓글 관리)
    // 차단 기능, 비정상적인 활동에 대한 차단(다른 사용자들로 부터 신고를 N회 이상 당한 계정에 대한 조취)

}
