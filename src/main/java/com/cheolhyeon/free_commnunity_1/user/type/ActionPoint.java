package com.cheolhyeon.free_commnunity_1.user.type;

import lombok.Getter;

@Getter
public enum ActionPoint {
    POST(1, "게시글 생성 포인트"),
    COMMENT(1, "댓글 생성 포인트");

    private final int point;
    private final String description;

    ActionPoint(int point, String description) {
        this.point = point;
        this.description = description;
    }
}
