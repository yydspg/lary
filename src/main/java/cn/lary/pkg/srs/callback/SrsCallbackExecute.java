package cn.lary.pkg.srs.callback;

import cn.lary.kit.StringKit;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.stream.dto.JoinLiveCacheDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.pkg.srs.config.SRS;
import cn.lary.pkg.srs.dto.OnPlayDTO;
import cn.lary.pkg.srs.dto.OnPublishDTO;
import cn.lary.pkg.srs.dto.OnStopDTO;
import cn.lary.pkg.srs.dto.OnUnpublishDTO;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SrsCallbackExecute implements SrsCallback {

    private final RedisCache redisCache;
    private final EventService eventService;
    private final KVBuilder kvBuilder;
    private final StreamRecordService streamRecordService;
    //external
    private final WKMessageService wkMessageService;

    @Override
    public int onPublish(OnPublishDTO dto) {
        Map<String, String> args = dto.parseParams(dto.getParam());
        String token = args.get("token");
        int uid = Integer.parseInt( args.get("uid"));
        String eventId = args.get("event");

        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(uid));
        if (map == null) {
            return SRS.CallBackStatus.fail;
        }
        LiveCacheDTO cache = LiveCacheDTO.of(map);
        // check ip
        if (StringKit.diff(dto.getIp(), cache.getIp())) {
            return SRS.CallBackStatus.fail;
        }
        // check token
        if (StringKit.diff(token, cache.getSrsToken())) {
            return SRS.CallBackStatus.fail;
        }

        //update cache
        LiveCacheDTO updateRecord = new LiveCacheDTO().setSrsClientId(dto.getClientId()).setSrsStreamId(dto.getStreamId())
                .setSrsTcUrl(dto.getTcUrl()).setSrsServerId(dto.getServerId()).setSrsStreamUrl(dto.getStreamUrl());
        redisCache.setHash(kvBuilder.goLiveK(uid),kvBuilder.goLiveV(updateRecord));
        streamRecordService.update(new LambdaUpdateWrapper<StreamRecord>().eq(StreamRecord::getStreamId,cache.getStreamId()).set(StreamRecord::getStatus, Lary.Stream.Status.up));
        // close event
        eventService.commit(Integer.parseInt(eventId));
        // update stream record status
        return SRS.CallBackStatus.ok;
    }

    @Override
    @Transactional
    public int onUnPublish(OnUnpublishDTO dto) {

        Map<String, String> args = dto.parseParams(dto.getParam());

        String token = args.get("token");
        int uid = Integer.parseInt( args.get("uid"));
        String eventId = args.get("event");
        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(uid));
        LiveCacheDTO cache = LiveCacheDTO.of(map);

        // check ip
        if (StringKit.diff(dto.getIp(), cache.getIp())) {
            log.error("srs unpublish fail when check ip:{},uid:{}",dto.getIp(),uid);
            return SRS.CallBackStatus.fail;
        }
        // check token
        if (StringKit.diff(token, cache.getSrsToken())) {
            log.error("srs unpublish fail when check token:{},uid:{}",token,uid);
            return SRS.CallBackStatus.fail;
        }
        redisCache.del(kvBuilder.goLiveK(uid));
        // close event
        eventService.commit(Integer.parseInt(eventId));
       return SRS.CallBackStatus.ok;
    }

    @Override
    public int onPlay(OnPlayDTO dto) {
        Map<String, String> args = dto.parseParams(dto.getParam());
        String token = args.get("token");
        int uid = Integer.parseInt( args.get("uid"));
        Map<Object, Object> map = redisCache.getHash(kvBuilder.joinLiveK(uid));
        if (map == null) {
            return SRS.CallBackStatus.fail;
        }
        JoinLiveCacheDTO cache = JoinLiveCacheDTO.of(map);
        // check ip
        if (StringKit.diff(dto.getIp(), cache.getIp())) {
            return SRS.CallBackStatus.fail;
        }
        // check token
        if (StringKit.diff(token, cache.getSrsToken())) {
            return SRS.CallBackStatus.fail;
        }
        // set data to cache
        JoinLiveCacheDTO updateRecord = new JoinLiveCacheDTO().setSrsClientId(dto.getClientId())
                .setSrsServerId(dto.getServerId());
        long expire = 30;
        redisCache.setHash(kvBuilder.joinLiveK(uid),kvBuilder.joinLiveV(updateRecord),expire, TimeUnit.MINUTES);
       return SRS.CallBackStatus.ok;
    }

    /**
     * 直接返回，对redis内存的清理都在 业务上，不然还要维护这个长事件
     * @param dto {@link OnStopDTO}
     * @return ok
     */
    @Override
    public int onStop(OnStopDTO dto) {
        return SRS.CallBackStatus.ok;
    }
}
