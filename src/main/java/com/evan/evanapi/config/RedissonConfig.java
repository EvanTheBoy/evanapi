package com.evan.evanapi.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String port;
    private String host;
    @Bean
    public RedissonClient redissonClient() {
        String redisAddress = String.format("redis://%s:%s", host, port);
        // 1. Create config object
        Config config = new Config();
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);
        // 2. Create Redisson instance
        // Sync and Async API
        return Redisson.create(config);
    }
}
