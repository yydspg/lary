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
    private String loginUserPrefix;
    private String loginUserTokenPrefix;
    private String registerPrefix;
    private String friendApplyTokenCachePrefix;
    private String danmakuTokenCachePrefix;
    private String goLivePrefix;
    private String joinLivePrefix;
    private String streamRecordPrefix;
    private String smsAddDevicePrefix;
    // 单位为 s
    private long smsAddDeviceExpire;
    private long loginDeviceCacheExpire;
    private long loginUserExpire;
    private long loginUserTokenExpire;
    private long registerExpire;
    private long tokenExpire;
    private long friendApplyExpire;
    private long nameCacheExpire;
    private long danmakuChannelExistsExpire; //days
}
