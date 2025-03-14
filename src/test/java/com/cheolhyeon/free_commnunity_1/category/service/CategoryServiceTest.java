package com.cheolhyeon.free_commnunity_1.category.service;

import com.cheolhyeon.free_commnunity_1.category.repository.CategoryRepository;
import com.cheolhyeon.free_commnunity_1.category.repository.entity.CategoryEntity;
import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    @Test
    @DisplayName("categoryId로 1을 넣으면 GENERAL을 반환한다")
    void getGeneralCategory() {
        //given
        CategoryEntity general = CategoryEntity.builder()
                .name(Category.GENERAL)
                .build();
        given(categoryRepository.findById(1L)).willReturn(Optional.of(general));
        //when
        Category category = categoryService.getCategory(1L);

        //then
        assertThat(category).isEqualTo(Category.GENERAL);
    }

    @Test
    @DisplayName("categoryId로 2을 넣으면 ENTERTAINMENT 반환한다")
    void getEntertainmentCategory() {
        //given

        given(categoryRepository.findById(2L))
                .willReturn(Optional.of(CategoryEntity.builder()
                        .name(Category.ENTERTAINMENT)
                        .build()));
        //when
        Category category = categoryService.getCategory(2L);

        //then
        assertThat(category).isEqualTo(Category.ENTERTAINMENT);
    }

    @Test
    @DisplayName("categoryId로 3을 넣으면 ENTERTAINMENT 반환한다")
    void getLifeCategory() {
        //given
        given(categoryRepository.findById(3L))
                .willReturn(Optional.of(CategoryEntity.builder()
                        .name(Category.LIFE)
                        .build()));
        //when
        Category category = categoryService.getCategory(3L);

        //then
        assertThat(category).isEqualTo(Category.LIFE);
    }
    @Test
    @DisplayName("categoryId로 4을 넣으면 SPORTS 반환한다")
    void getSpotsCategory() {
        //given
        given(categoryRepository.findById(4L))
                .willReturn(Optional.of(CategoryEntity.builder()
                        .name(Category.SPORTS)
                        .build()));
        //when
        Category category = categoryService.getCategory(4L);

        //then
        assertThat(category).isEqualTo(Category.SPORTS);
    }
    @Test
    @DisplayName("categoryId로 5을 넣으면 TIPS 반환한다")
    void getTipsCategory() {
        //given
        given(categoryRepository.findById(5L))
                .willReturn(Optional.of(CategoryEntity.builder()
                        .name(Category.TIPS)
                        .build()));
        //when
        Category category = categoryService.getCategory(5L);

        //then
        assertThat(category).isEqualTo(Category.TIPS);
    }

}