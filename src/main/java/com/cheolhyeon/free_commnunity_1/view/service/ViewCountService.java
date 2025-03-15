package com.cheolhyeon.free_commnunity_1.view.service;

import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountDistributedLockRepository;
import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ViewCountService {
    private static final int BACK_UP_BATCH_SIZE = 50;
    private static final Duration TTL = Duration.ofMinutes(3);
    private final ViewCountRedisRepository viewCountRedisRepository;
    private final ViewCountBackUpService viewCountBackUpService;
    private final ViewCountDistributedLockRepository viewCountDistributedLockRepository;

    public Long increase(Long postId, Long userId) {
        if (viewCountDistributedLockRepository.lock(postId, userId, TTL)) {
            return viewCountRedisRepository.read(postId);
        }

        Long currentViewCount = viewCountRedisRepository.increase(postId);
        if (currentViewCount % BACK_UP_BATCH_SIZE == 0) {
            viewCountBackUpService.backUp(postId, userId);
        }
        return currentViewCount;
    }

    public Long getCurrentViewCount(Long postId) {
        return viewCountRedisRepository.read(postId);
    }
}
