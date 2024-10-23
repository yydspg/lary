package cn.lary.module.common.server;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RedisBizConfig {

    private final String friendApplyTokenCachePrefix = "lary:";
    private final String tokenCachePrefix = "lary:";
    private final String loginDeviceCachePrefix = "lary:device";
    private final String loginUserPrefix = "lary:";
    private final String loginUserTokenPrefix = "lary:";
    private final String registerPrefix = "lary:register";
    private final String destroyPrefix = "lary:register";
    private final String danmakuTokenCachePrefix = "lary:danmaku";
    private final String goLivePrefix  = "lary:goLive";
    private final String joinLivePrefix = "lary:joinLive";
    private final String streamRecordPrefix = "lary:streamRecord";
    private final String smsAddDevicePrefix = "lary:smsAddDevice";
    private final String rafflePrefix = "lary:raffle:";
    private final String raffleListPrefix = "lary:raffle:list:";
    private final String redPacketPrefix = "lary:redPacket:";
    // 单位为 s
    private final long danmakuChannelExistsExpire = 100;
    private final long smsAddDeviceExpire = 100;
    private final long loginDeviceCacheExpire = 100;
    private final long loginUserExpire = 100;
    private final long loginUserTokenExpire = 100;
    private final long registerExpire = 100;
    private final long destroyExpire = 100;
    private final long tokenExpire = 100;
    private final long friendApplyExpire = 100;
    private final long nameCacheExpire = 100;

}
