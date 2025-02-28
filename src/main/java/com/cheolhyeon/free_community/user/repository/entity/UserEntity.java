package com.cheolhyeon.free_community.user.repository.entity;

import com.cheolhyeon.free_community.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;
    private Long actionPoint;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserEntity from(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.nickname = user.getNickname();
        userEntity.actionPoint = user.getActionPoint();
        userEntity.createdAt = user.getCreateAt();
        userEntity.updatedAt = user.getUpdateAt();
        return userEntity;
    }

    public User toModel() {
        return User.builder()
                .id(id)
                .nickname(nickname)
                .actionPoint(actionPoint)
                .createAt(createdAt)
                .updateAt(updatedAt)
                .build();
    }
}
