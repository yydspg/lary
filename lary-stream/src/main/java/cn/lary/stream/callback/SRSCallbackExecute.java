package cn.lary.stream.callback;

import cn.lary.common.kit.StringKit;
import cn.lary.external.srs.config.SRS;
import cn.lary.external.srs.dto.OnPlayDTO;
import cn.lary.external.srs.dto.OnPublishDTO;
import cn.lary.external.srs.dto.OnStopDTO;
import cn.lary.external.srs.dto.OnUnpublishDTO;
import cn.lary.stream.component.LiveCacheComponent;
import cn.lary.stream.dto.LiveCache;
import cn.lary.stream.entity.StreamRecord;
import cn.lary.stream.service.StreamRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SRSCallbackExecute implements SRSCallback {


//    private final EventService eventService;
    private final StreamRecordService streamRecordService;
    private final LiveCacheComponent liveCacheComponent;
    private final TransactionTemplate transactionTemplate;
//    private final UserCacheComponent userCacheComponent;

    @Override
    public int onPublish(OnPublishDTO dto) {
        Map<String, String> args = dto.parseParams(dto.getParam());
        String token = args.get("srsToken");
        long sid = Long.parseLong( args.get("sid"));
        String eventId = args.get("event");
        LiveCache cache = liveCacheComponent.getLive(sid);
        if (cache == null) {
            return SRS.CALLBACK_STATS.FAIL;
        }
        if (StringKit.diff(dto.getIp(), cache.getIp())) {
            return SRS.CALLBACK_STATS.FAIL;
        }
        if (StringKit.diff(token, cache.getSrsToken())) {
            return SRS.CALLBACK_STATS.FAIL;
        }
        LiveCache updateRecord = new LiveCache()
                .setIp(cache.getIp())
                .setSrsToken(token)
                .setSid(cache.getSid())
                .setRid(cache.getRid())
                .setCid(cache.getCid())
                .setLevel(cache.getLevel())
                .setIdentify(cache.getIdentify())
                .setSrsClientId(dto.getClientId())
                .setSrsStreamId(dto.getStreamId())
                .setSrsTcUrl(dto.getTcUrl())
                .setSrsServerId(dto.getServerId())
                .setSrsStreamUrl(dto.getStreamUrl());
        liveCacheComponent.setLive(sid, updateRecord);
        transactionTemplate.executeWithoutResult(status -> {
//            streamRecordService.lambdaUpdate()
//                    .set(StreamRecord::getStatus, LARY.STREAM.STATUS.UP)
//                    .eq(StreamRecord::getSid,cache.getSid())
//                    .update();
//            eventService.commit(Integer.parseInt(eventId));
        });
        return SRS.CALLBACK_STATS.OK;
    }

    @Override
    @Transactional
    public int onUnPublish(OnUnpublishDTO dto) {

        Map<String, String> args = dto.parseParams(dto.getParam());

        String token = args.get("srsToken");
        long sid = Integer.parseInt( args.get("sid"));
        String eventId = args.get("event");
        LiveCache cache = liveCacheComponent.getLive(sid);
        if (StringKit.diff(dto.getIp(), cache.getIp())) {
            log.error("srs unpublish FAIL when check ip:{},uid:{}",dto.getIp(),sid);
            return SRS.CALLBACK_STATS.FAIL;
        }
        if (StringKit.diff(token, cache.getSrsToken())) {
            log.error("srs unpublish FAIL when check srsToken:{},uid:{}",token,sid);
            return SRS.CALLBACK_STATS.FAIL;
        }
//        eventService.commit(Long.parseLong(eventId));
       return SRS.CALLBACK_STATS.OK;
    }

    @Override
    public int onPlay(OnPlayDTO dto) {
        Map<String, String> args = dto.parseParams(dto.getParam());
        String token = args.get("srsToken");
        long sid = Long.parseLong( args.get("sid"));
        long uid = Long.parseLong( args.get("uid"));
        int  flag = Integer.parseInt( args.get("flag"));
        LiveCache live = liveCacheComponent.getLive(sid);
        if (live == null) {
            return SRS.CALLBACK_STATS.FAIL;
        }
//        UserCache cache = userCacheComponent.getData(uid,flag);
//
//        if (StringKit.diff(dto.getIp(), cache.getIp())) {
//            return SRS.CALLBACK_STATS.FAIL;
//        }
//        if (StringKit.diff(token, cache.getSrsToken())) {
//            return SRS.CALLBACK_STATS.FAIL;
//        }
//        UserCache updateRecord = new UserCache(cache)
//                .setSrsStreamId(dto.getStreamId())
//                .setSrsClientId(dto.getClientId())
//                .setSrsServerId(dto.getServerId());
//        userCacheComponent.setData(uid,flag, updateRecord);
        return SRS.CALLBACK_STATS.OK;
    }

    /**
     * 直接返回，对redis内存的清理都在 业务上，不然还要维护这个长事件
     * @param dto {@link OnStopDTO}
     * @return OK
     */
    @Override
    public int onStop(OnStopDTO dto) {
        return SRS.CALLBACK_STATS.OK;
    }
}
