package com.cheolhyeon.free_commnunity_1.comment.service;

import com.cheolhyeon.free_commnunity_1.comment.repository.entity.CommentEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cheolhyeon.free_commnunity_1.comment.repository.entity.QCommentEntity.commentEntity;

@Repository
public class CommentQueryRepository {
    private final JPAQueryFactory query;

    public CommentQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public Page<CommentEntity> readAllCommentsAsPage(Long postId, Pageable pageable) {
        List<CommentEntity> content = query
                .select(commentEntity)
                .from(commentEntity)
                .where(commentEntity.postId.eq(postId))
                .orderBy(commentEntity.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = countByCondOptimization(content, postId, (long) pageable.getPageNumber(), (long) pageable.getPageSize());
        return new PageImpl<>(content, pageable, totalCount);
    }

    private Long countByCondOptimization(List<?> content, Long postId, Long page, Long size) {
        boolean isPaged = page != null && size != null && page > 0 && size > 0;
        long offset = isPaged ? (page - 1) * size : 0L;
        long contentSize = content.size();

        if (!isPaged || offset == 0) {
            if (!isPaged || size > contentSize) {
                return this.countByCond(postId);
            }
            return contentSize;
        }

        if (contentSize != 0 && size > contentSize) {
            return this.countByCond(postId);
        }

        return offset + contentSize;
    }

    private Long countByCond(Long postId) {
        return query
                .select(commentEntity.count())
                .from(commentEntity)
                .where(commentEntity.postId.eq(postId))
                .fetchOne();
    }
}
