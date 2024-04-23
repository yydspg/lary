package cn.lary.test.config;

import cn.lary.core.lock.exec.impl.RedissonLockExec;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author paul 2024/4/23
 */
@Configuration
public class RedissonLockAutoConfig {
    @Bean
    @Order(100)
    public RedissonLockExec redissonLockExecutor(RedissonClient redissonClient) {
        return new RedissonLockExec(redissonClient);
    }
}
