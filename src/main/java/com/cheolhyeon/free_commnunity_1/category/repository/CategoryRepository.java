package com.cheolhyeon.free_commnunity_1.category.repository;

import com.cheolhyeon.free_commnunity_1.category.repository.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
