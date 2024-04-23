package cn.lary.test.config;

import cn.lary.core.lock.aop.LockAdvisor;
import cn.lary.core.lock.aop.impl.LockOpsInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author paul 2024/4/24
 */
@Configuration
public class LockConfig {
    @Bean
    public LockAdvisor lockAdvisor(LockOpsInterceptor lockOpsInterceptor){
        return new LockAdvisor(lockOpsInterceptor, Ordered.HIGHEST_PRECEDENCE);
    }
}
