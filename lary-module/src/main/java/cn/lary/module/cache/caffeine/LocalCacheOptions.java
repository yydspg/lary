package cn.lary.module.cache.caffeine;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.TimeUnit;

@Data
@Accessors(chain = true)
public class LocalCacheOptions<K,V> {

    private String instanceName;

    private int expireAfterWrite;

    private TimeUnit expireAfterWriteTimeUnit;

    private long maximumSize;

    private int policy;

}
