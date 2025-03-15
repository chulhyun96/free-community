package com.cheolhyeon.free_commnunity_1.post.service;

import com.cheolhyeon.free_commnunity_1.category.service.type.Category;
import com.cheolhyeon.free_commnunity_1.post.controller.response.PostSearchResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.search.PostSearchCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.cheolhyeon.free_commnunity_1.category.repository.entity.QCategoryEntity.categoryEntity;
import static com.cheolhyeon.free_commnunity_1.post.repository.entity.QPostEntity.postEntity;
import static com.cheolhyeon.free_commnunity_1.view.repository.entity.QViewCountEntity.viewCountEntity;

@Repository
public class PostQueryRepository {
    private final JPAQueryFactory query;

    public PostQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public Slice<PostSearchResponse> searchBySearchCondInfiniteScroll(PostSearchCondition condition, Pageable pageable, String sort) {
        List<PostSearchResponse> content = getSearchResponsesAsInfinite(condition, pageable, sort);

        boolean hasNext = hasNextPage(content.size(), pageable);
        if (hasNext) {
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    public Page<PostSearchResponse> searchByCond(PostSearchCondition condition, Pageable pageable, String sort) {
        List<PostSearchResponse> content = getPostSearchResponses(condition, pageable, sort);
        Long count = countByCondOptimization(content, condition, (long) pageable.getPageNumber(), (long) pageable.getPageSize());
        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    private List<PostSearchResponse> getSearchResponsesAsInfinite(PostSearchCondition condition, Pageable pageable, String sort) {
        return query
                .select(Projections.constructor(
                        PostSearchResponse.class,
                        postEntity.id,
                        postEntity.title,
                        categoryEntity.name.stringValue().as("categoryName"),
                        Expressions.constant(0L),
                        postEntity.createdAt
                ))
                .from(postEntity)
                .leftJoin(categoryEntity)
                .on(postEntity.categoryId.eq(categoryEntity.id))
                .leftJoin(viewCountEntity)
                .on(postEntity.id.eq(viewCountEntity.postId))
                .where(searchConditionAll(condition))
                .orderBy(sortDirection(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
    }

    private boolean hasNextPage(int size, Pageable pageable) {
        return size > pageable.getPageSize();
    }

    private List<PostSearchResponse> getPostSearchResponses(PostSearchCondition condition, Pageable pageable, String sort) {
        return query
                .select(Projections.constructor(
                        PostSearchResponse.class,
                        postEntity.id,
                        postEntity.title,
                        categoryEntity.name.stringValue().as("categoryName"),
                        Expressions.constant(0L),
                        postEntity.createdAt
                ))
                .from(postEntity)
                .leftJoin(categoryEntity)
                .on(postEntity.categoryId.eq(categoryEntity.id))
                .leftJoin(viewCountEntity)
                .on(postEntity.id.eq(viewCountEntity.postId))
                .where(searchConditionAll(condition))
                .orderBy(sortDirection(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private OrderSpecifier<?>[] sortDirection(String sort) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier<?>[]{postEntity.createdAt.desc()};
        }
        return new OrderSpecifier<?>[]{viewCountEntity.viewCount.desc(), postEntity.createdAt.desc()};
    }

    private BooleanExpression searchConditionAll(PostSearchCondition condition) {
        BooleanExpression titleCond = titleLike(condition.getTitle());
        BooleanExpression categoryCond = isCategoryNameNotNull(condition.getCategoryName());

        return Stream.of(titleCond, categoryCond)
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .orElseGet(this::defaultSearchCond);
    }

    private BooleanExpression defaultSearchCond() {
        return null;
    }

    private BooleanExpression titleLike(String title) {
        return StringUtils.hasText(title) ? postEntity.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression isCategoryNameNotNull(String categoryName) {
        return StringUtils.hasText(categoryName) ? categoryEntity.name.eq(Category.valueOf(categoryName)) : null;
    }

    private Long countByCondOptimization(List<?> content, PostSearchCondition cond, Long page, Long size) {
        boolean isPaged = page != null && size != null && page > 0 && size > 0;
        long offset = isPaged ? (page - 1) * size : 0L;
        long contentSize = content.size();

        if (!isPaged || offset == 0) {
            if (!isPaged || size > contentSize) {
                return contentSize;
            }
            return this.countByCond(cond);
        }

        if (contentSize != 0 && size > contentSize) {
            return offset + contentSize;
        }
        return this.countByCond(cond);
    }

    private Long countByCond(PostSearchCondition condition) {
        return query.select(postEntity.count())
                .from(postEntity)
                .leftJoin(categoryEntity)
                .on(postEntity.categoryId.eq(categoryEntity.id))
                .where(searchConditionAll(condition)
                ).fetchOne();
    }
}
