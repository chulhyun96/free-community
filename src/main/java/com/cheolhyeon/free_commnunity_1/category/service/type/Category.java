package com.cheolhyeon.free_commnunity_1.category.service.type;

import lombok.Getter;

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
        for (Category category : Category.values()) {
            if (category.equals(name))
                return category;
        }
        return Category.GENERAL;
    }

}
