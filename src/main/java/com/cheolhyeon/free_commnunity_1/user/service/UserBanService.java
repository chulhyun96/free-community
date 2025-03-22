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

    public String getBanTTL(Long userId) {
        Long banTTL = banRedisRepository.getBanTTL(userId);
        long hours = banTTL / 3600;
        long minutes = (banTTL % 3600) / 60;
        return String.format("%d시간 %d분", hours, minutes);
    }

    public boolean isBanned(Long userId) {
        return banRedisRepository.isUserBanned(userId);
    }
}
