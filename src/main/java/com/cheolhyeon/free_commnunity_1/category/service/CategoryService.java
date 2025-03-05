package com.cheolhyeon.free_commnunity_1.category.service;

import com.cheolhyeon.free_commnunity_1.category.repository.CategoryRepository;
import com.cheolhyeon.free_commnunity_1.category.repository.entity.CategoryEntity;
import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category findById(Long id) {
        CategoryEntity entity = categoryRepository.findById(id).orElseThrow();
        return Category.get(entity.getName());
    }
}
