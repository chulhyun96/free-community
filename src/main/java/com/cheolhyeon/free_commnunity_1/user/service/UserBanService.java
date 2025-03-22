package com.cheolhyeon.free_commnunity_1.user.service;

import com.cheolhyeon.free_commnunity_1.user.repository.RedisUserBanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserBanService {
    private final RedisUserBanRepository banRedisRepository;

    private static final Duration TTL = Duration.ofDays(3);
    private static final int THRESHOLD = 5;

    public boolean ban(Long userId, Long reportCount) {
        if (reportCount >= THRESHOLD) {
            banRedisRepository.ban(userId, TTL);
            return true;
        }
        return false;
    }

    public boolean isBanned(Long userId) {
        return banRedisRepository.isUserBanned(userId);
    }
}
