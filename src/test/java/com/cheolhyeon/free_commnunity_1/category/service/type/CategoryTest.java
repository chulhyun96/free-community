package com.cheolhyeon.free_commnunity_1.category.service.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class CategoryTest {

    @Test
    @DisplayName("카테고리 Category.GENERAL을 넣으면 해당 Category가 반환된다.")
    void getGeneralCategory() {
        //given
        Category general = Category.get(Category.GENERAL);
        //when
        //then
        assertThat(general).isInstanceOf(Category.class);
        assertThat(general.getName()).isEqualTo("자유게시판");
    }

    @Test
    @DisplayName("카테고리 Category.ENTERTAINMENT을 넣으면 해당 Category가 반환된다.")
    void getEntertainmentCategory() {
        //given
        Category general = Category.get(Category.ENTERTAINMENT);
        //when
        //then
        assertThat(general).isInstanceOf(Category.class);
        assertThat(general.getName()).isEqualTo("연애");
    }

    @Test
    @DisplayName("카테고리 Category.LIFE 넣으면 해당 Category가 반환된다.")
    void getLifeCategory() {
        //given
        Category general = Category.get(Category.LIFE);
        //when
        //then
        assertThat(general).isInstanceOf(Category.class);
        assertThat(general.getName()).isEqualTo("고민");
    }

    @Test
    @DisplayName("카테고리 Category.SPORTS 넣으면 해당 Category가 반환된다.")
    void getSportsCategory() {
        //given
        Category general = Category.get(Category.SPORTS);
        //when
        //then
        assertThat(general).isInstanceOf(Category.class);
        assertThat(general.getName()).isEqualTo("스포츠");
    }

    @Test
    @DisplayName("카테고리 Category.TIPS 넣으면 해당 Category가 반환된다.")
    void getTipsCategory() {
        //given
        Category general = Category.get(Category.TIPS);
        //when
        //then
        assertThat(general).isInstanceOf(Category.class);
        assertThat(general.getName()).isEqualTo("꿀팁");
    }

    @Test
    @DisplayName("카테고리가 null 일 경우 해당 GENERAL이 반환된다.")
    void nullInputGetGeneral() {
        //given
        Category general = Category.get(null);
        //when
        //then
        assertThat(general).isInstanceOf(Category.class);
        assertThat(general.getName()).isEqualTo("자유게시판");
    }
}