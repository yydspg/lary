package cn.lary.core.ratelimiter.config;

import cn.lary.core.ratelimiter.aop.RateAdvisor;
import cn.lary.core.ratelimiter.aop.RateInterceptor;
import cn.lary.core.ratelimiter.builder.RateKeyBuilder;
import cn.lary.core.ratelimiter.builder.impl.DefaultRateKeyBuilder;
import cn.lary.core.util.SpringContextKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author paul 2024/4/25
 */

@Configuration
public class RateAutoConfig {

    @Bean(name = "slidingWindowLuaScript")
    public DefaultRedisScript<Boolean> slidingWinLimitScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/slidingWindow.lua")));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }
    @Bean(name = "tokenBucketLuaScript")
    public DefaultRedisScript<Boolean> tokenBucketLimitScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/tokenBucket.lua")));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }
    @Bean
    @ConditionalOnMissingBean
    public RateKeyBuilder rateKeyBuilder(){
        return new DefaultRateKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public RateInterceptor rateInterceptor(RateKeyBuilder rateKeyBuilder){
        return new RateInterceptor(rateKeyBuilder);
    }
    @Bean
    @ConditionalOnMissingBean
    public RateAdvisor rateAdvisor(RateInterceptor rateInterceptor){
        return new RateAdvisor(rateInterceptor);
    }
}
