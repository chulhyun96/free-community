package com.cheolhyeon.free_commnunity_1.view.service;

import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountBackUpRepository;
import com.cheolhyeon.free_commnunity_1.view.repository.entity.ViewCountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ViewCountBackUpService {
    private final ViewCountBackUpRepository viewCountBackUpRepository;

    @Transactional
    public void backUp(Long postId, Long viewCount) {
        // 요놈이 백업을 하는것이다, -> 여기서 ViewCountEntity를 저장할 때 엔티티를 생성하고 DB에 저장
        int result = viewCountBackUpRepository.updateViewCount(postId, viewCount);
        if (result == 0) {
            viewCountBackUpRepository.findById(postId)
                    .ifPresentOrElse(ignored -> {},
                            () -> viewCountBackUpRepository.save(ViewCountEntity.init(postId, viewCount))
                    );
        }
    }
}
