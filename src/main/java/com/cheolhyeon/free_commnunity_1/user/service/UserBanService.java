package com.cheolhyeon.free_commnunity_1.user.service;

import com.cheolhyeon.free_commnunity_1.user.repository.RedisUserBanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBanService {
    private final RedisUserBanRepository redisUserBanRepository;

}
