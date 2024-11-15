package cn.lary.module.cache.component;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class SMSCacheComponent {

    private final RedissonClient redisson;


    public final void store(String prefix, String phone,String token, Duration duration) {
        redisson.<String>getBucket(prefix+phone).set(token, duration);
    }

    public final void drop(String prefix,String phone) {
        redisson.<String>getBucket(prefix+phone).delete();
    }
    public final String get(String prefix,String phone) {
        return redisson.<String>getBucket(prefix+phone).get();
    }

}
