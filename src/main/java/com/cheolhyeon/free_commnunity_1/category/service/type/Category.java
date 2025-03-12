package com.cheolhyeon.free_commnunity_1.category.service.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Category {
    GENERAL(1L,"자유게시판"),
    ENTERTAINMENT(2L,"연애"),
    LIFE(3L,"고민"),
    SPORTS(4L,"스포츠"),
    TIPS(5L,"꿀팁");

    private final Long id;
    private final String name;

    Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Category get(Category name) {
       return Arrays.stream(values())
                .filter(category -> category.equals(name))
                .findFirst()
                .orElse(Category.GENERAL);
    }

}
