package cn.lary.module.common.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lary.redis")
public class RedisBizConfig {
    private String tokenCachePrefix;
    private String loginDeviceCachePrefix;
    private String uidTokenCachePrefix;
    private String friendApplyTokenCachePrefix;
    private String danmakuTokenCachePrefix;
    // 单位为 s
    private long loginDeviceCacheExpire;
    private long tokenExpire;
    private long friendApplyExpire;
    private long nameCacheExpire;
    private long danmakuChannelExistsExpire; //days
}
