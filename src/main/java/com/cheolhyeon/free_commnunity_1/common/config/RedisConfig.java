package com.cheolhyeon.free_commnunity_1.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory viewCountRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(0);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Primary
    public StringRedisTemplate viewCountRedisTemplate() {
        return new StringRedisTemplate(viewCountRedisConnectionFactory());
    }

    @Bean
    public RedisConnectionFactory commentLikeRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(1);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean(name = "redisTemplate")
    public StringRedisTemplate commentLikeRedisTemplate() {
        return new StringRedisTemplate(commentLikeRedisConnectionFactory());
    }
}
