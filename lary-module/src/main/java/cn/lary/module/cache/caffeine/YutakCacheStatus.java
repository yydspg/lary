package cn.lary.module.cache.caffeine;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class YutakCacheStatus {

    private CacheStats status;

    private String instanceName;

    private long timestamp;

    public YutakCacheStatus() {}

    public YutakCacheStatus(CacheStats status) {
        this.status = status;
    }

}
