package cn.lary.module.danmaku.serviceImpl;

import cn.lary.kit.StringKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.danmaku.service.DanmakuService;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.entity.core.WKChannel;
import cn.lary.pkg.wk.dto.channel.ChannelCreateDTO;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DanmakuServiceImpl implements DanmakuService {
    private final WKChannelService wkChannelService;
    private final WKMessageService wkMessageService;
    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;
    private final RoomService roomService;
    private final StreamRecordService streamRecordService;
    @Override
    public WKChannel getDanmakuChannel(String uid) {
        return null;
    }

    @Override
    public boolean exist(String channelId,String uid) {
        String channel = redisCache.get(kvBuilder.buildDanmakuChannelTokenKey(uid));
        return StringKit.same(channel, channelId);
    }

    @Override
    public void addCache(String channelId, String uid) {
        redisCache.set(kvBuilder.buildDanmakuChannelTokenKey(uid),kvBuilder.buildDanmakuChannelTokenValue(channelId), 1L, TimeUnit.DAYS);
    }
}
