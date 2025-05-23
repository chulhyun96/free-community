package com.cheolhyeon.free_commnunity_1.view.service;

import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountBackUpRepository;
import com.cheolhyeon.free_commnunity_1.view.repository.entity.ViewCountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ViewCountBackUpService {
    private final ViewCountBackUpRepository viewCountBackUpRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void backUp(Long postId, Long viewCount) {
        int result = viewCountBackUpRepository.updateViewCount(postId, viewCount);
        if (result == 0) {
            viewCountBackUpRepository.findById(postId)
                    .ifPresentOrElse(ignored -> {
                            },
                            () -> viewCountBackUpRepository.save(ViewCountEntity.init(postId, viewCount))
                    );
        }
    }
}
