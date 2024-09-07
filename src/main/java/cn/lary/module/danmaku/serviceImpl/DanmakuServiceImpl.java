package cn.lary.module.danmaku.serviceImpl;

import cn.lary.kit.StringKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.config.WkConfig;
import cn.lary.module.danmaku.service.DanmakuService;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.entity.Request.channel.ChannelCreateReq;
import cn.lary.pkg.wk.entity.core.Channel;
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
    public Channel getDanmakuChannel(String uid) {

        if (StringKit.isEmpty(uid)) {
            return null;
        }
        // build data channel
        StreamRecord streamRecord = streamRecordService.getOne(new LambdaQueryWrapper<StreamRecord>().eq(StreamRecord::getUid, uid).select(StreamRecord::getStreamId), false);
        if (streamRecord == null) {
            // please
            log.error("please add room first");
            return null;
        }
        boolean exist = exist(streamRecord.getChannelId(), uid);
        Channel channel = null;
        // already exists
        if (exist) {
            channel = new Channel().setChanelID(streamRecord.getChannelId()).setChannelType(WK.ChannelType.data);
            return channel;
        }else {
            // create wk channel
            String channelId = UUIDKit.getUUID();
            channel = new Channel().setChanelID(channelId).setChannelType(WK.ChannelType.data);
            ChannelCreateReq req = new ChannelCreateReq();
            req.setChanelID(channelId).setChannelType(WK.ChannelType.data);
            wkChannelService.createOrUpdate(req);
            // add in redis
            // TODO  :  这里可以考虑下以后和 stream url的聚合 ，用 redis的hash存
            addCache(channelId, uid);
        }
        return channel;
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
