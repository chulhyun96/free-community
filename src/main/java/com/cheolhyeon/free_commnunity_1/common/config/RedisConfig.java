package com.cheolhyeon.free_commnunity_1.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactoryForSpring() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisConnectionFactoryForSpring") RedisConnectionFactory redisConnectionFactoryForSpring) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryForSpring);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisConnectionFactory viewCountRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(0);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public StringRedisTemplate viewCountRedisTemplate(@Qualifier("viewCountRedisConnectionFactory") RedisConnectionFactory viewCountRedisConnectionFactory) {
        return new StringRedisTemplate(viewCountRedisConnectionFactory);
    }

    @Bean
    public RedisConnectionFactory commentLikeRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(1);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public StringRedisTemplate commentLikeRedisTemplate(@Qualifier("commentLikeRedisConnectionFactory") RedisConnectionFactory commentLikeRedisConnectionFactory) {
        return new StringRedisTemplate(commentLikeRedisConnectionFactory);
    }

    @Bean
    public RedisConnectionFactory postLikeRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(2);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public StringRedisTemplate postLikeRedisTemplate(@Qualifier("postLikeRedisConnectionFactory") RedisConnectionFactory commentLikeRedisConnectionFactory) {
        return new StringRedisTemplate(commentLikeRedisConnectionFactory);
    }

    @Bean
    public RedisConnectionFactory hotPostRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(3);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public StringRedisTemplate hotPostRedisTemplate(@Qualifier("hotPostRedisConnectionFactory") RedisConnectionFactory commentLikeRedisConnectionFactory) {
        return new StringRedisTemplate(commentLikeRedisConnectionFactory);
    }

}
