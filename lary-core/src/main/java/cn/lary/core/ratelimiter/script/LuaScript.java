package cn.lary.core.ratelimiter.script;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author paul 2024/4/15
 */
@Configuration
public class LuaScript {
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

}
