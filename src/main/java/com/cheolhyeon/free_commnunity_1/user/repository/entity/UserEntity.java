package com.cheolhyeon.free_commnunity_1.user.repository.entity;


import com.cheolhyeon.free_commnunity_1.user.domain.User;
import com.cheolhyeon.free_commnunity_1.user.type.ActionPoint;
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
        UserEntity entity = new UserEntity();
        entity.nickname = user.getNickname();
        entity.actionPoint = user.getActionPoint();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        return entity;
    }

    public User toModel() {
        return User.builder()
                .id(id)
                .nickname(nickname)
                .actionPoint(actionPoint)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void update(User model) {
        this.nickname = model.getNickname();
        this.updatedAt = model.getUpdatedAt();
    }

    public void allocateActionPoint(ActionPoint actionPoint) {
        this.actionPoint += actionPoint.getPoint();
    }
}
