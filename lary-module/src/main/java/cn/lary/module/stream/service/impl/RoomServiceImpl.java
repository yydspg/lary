package cn.lary.module.stream.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.*;
import cn.lary.module.cache.dto.*;
import cn.lary.module.common.cache.CacheComponent;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.common.service.EventService;
import cn.lary.module.event.dto.GoLiveEventDTO;
import cn.lary.module.gift.service.AnchorFlowService;
import cn.lary.module.message.dto.stream.*;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.raffle.entity.RaffleEventCache;
import cn.lary.module.stream.dto.GoLiveDTO;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.mapper.RoomMapper;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.stream.vo.DownLiveVO;
import cn.lary.module.stream.vo.GoLiveVO;
import cn.lary.module.stream.vo.JoinLiveVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private final KVBuilder kvBuilder;
    private final CacheComponent cacheComponent;
    private final EventService eventService;
    private final StreamRecordService streamRecordService;

    private final FollowService followService;
    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;
    private final AnchorFlowService anchorFlowService;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            15, 20, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

        }
    });

    @Override
    public ResponsePair<JoinLiveVO> join(long toUid, String ip) {
        long uid = RequestContext.uid();
        String name = RequestContext.name();
        Follow relation = followService.lambdaQuery()
                .eq(Follow::getUid, uid)
                .eq(Follow::getUid, toUid)
                .one();
        boolean isFan = false;
        if(relation != null
                && relation.getStatus() == LARY.FOLLOW.STATUS.BLOCK
                && !relation.getIsDelete()){
            return BusinessKit.fail("你已被拉黑");
        }
        Map<Object,Object> args = cacheComponent.getHash(kvBuilder.goLiveK(toUid));
        if (args == null) {
            return BusinessKit.fail("no live info");
        }
        LiveCache liveCache = LiveCache.of(args);
        args = cacheComponent.getHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()));
        if (args == null) {
            return BusinessKit.fail("no stream record");
        }
        cacheComponent.incrHash(kvBuilder.streamRecordK(toUid, liveCache.getStreamId()),"watchNum");
        if (isFan) {
            cacheComponent.incrHash(kvBuilder.streamRecordK(toUid, liveCache.getStreamId()),"watchFanNum");
        }
        String authToken = UUIDKit.getUUID();
        // 30 minutes
        long expire = 30;
        JoinLiveCacheDTO dto = new JoinLiveCacheDTO()
                .setIp(ip)
//                .setStreamId(liveCache.getStreamId())
                .setSrsToken(authToken)
                .setSrsStreamId(liveCache.getSrsStreamId())
                .setIdentify(liveCache.getIdentify());
        cacheComponent.setHash(kvBuilder.joinLiveK(uid),kvBuilder.joinLiveV(dto),expire, TimeUnit.MINUTES);
        messageService.addSubscriber(new AddSubscribersDTO(liveCache.getDanmakuId(), List.of(uid)));
        messageService.send(new JoinRoomDTO(uid,name,liveCache.getDanmakuId()));
        String specify = liveCache.getIdentify();
        if (StringKit.isEmpty(specify)) {
            return BusinessKit.fail("stream url is empty");
        }
        RaffleEventCache raffle = null;
        RedPacketCacheDTO redPacket = null;
        Map<Object, Object> map = cacheComponent.getHash(kvBuilder.raffleK(toUid));
        if (map != null) {
//            raffle = RaffleEventCache.of(map);
        }
        map = cacheComponent.getHash(kvBuilder.redPacketK(toUid));
        if (map != null) {
            redPacket = RedPacketCacheDTO.of(map);
        }
        JoinLiveVO joinLiveVO = new JoinLiveVO()
                .setDanmakuId(liveCache.getDanmakuId())
                .setToken(authToken)
                .setSpecify(liveCache.getIdentify())
                .setRaffle(raffle)
                .setRedPacket(redPacket);
        return BusinessKit.ok(joinLiveVO);
    }

    @Override
    public ResponsePair<Void> leave() {
        long uid = RequestContext.uid();
        Map<Object, Object> map = cacheComponent.getHash(kvBuilder.joinLiveK(uid));
        if (map == null) {
            return BusinessKit.fail("no join live info");
        }
        cacheComponent.delete(kvBuilder.joinLiveK(uid));
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<DownLiveVO> end() {
        long uid = RequestContext.uid();
        Map<Object, Object> coll = null;
        coll = cacheComponent.getHash(kvBuilder.raffleK(uid));
        if (coll != null) {
            return BusinessKit.fail("存在未完成的抽奖活动");
        }
        coll = cacheComponent.getHash(kvBuilder.redPacketK(uid));
        if (coll != null) {
            return BusinessKit.fail("存在未结束的红包活动");
        }
        coll = cacheComponent.getHash(kvBuilder.goLiveK(uid));
        if (coll == null) {
            return BusinessKit.fail("no live info");
        }
        LiveCache liveCache = LiveCache.of(coll);
        Map<Object, Object> record = cacheComponent.getHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()));
        if (record == null) {
            return BusinessKit.fail("stream record not exist");
        }
        Room room = lambdaQuery()
                .select(Room::getLastLogin)
                .select(Room::getUid)
                .eq(Room::getUid, uid)
                .eq(Room::getIsBlock,false)
                .one();
        if (room == null) {
            return BusinessKit.fail("room not exist");
        }
        StreamRecordCacheDTO recordCache = StreamRecordCacheDTO.of(record);
        String token = UUIDKit.getUUID();
//        DownLiveEventDTO downLiveEventDTO = new DownLiveEventDTO(uid, liveCache.getStreamId(), liveCache.getDanmakuId());
        Long eventId = transactionTemplate.execute(status -> {
//            long event = eventService.begin(downLiveEventDTO);
            streamRecordService.lambdaUpdate()
                    .eq(StreamRecord::getStreamId, liveCache.getStreamId())
                    .set(StreamRecord::getStatus, LARY.Stream.Status.down)
                    .set(StreamRecord::getWatchFanNum, recordCache.getWatchFanNum())
                    .set(StreamRecord::getWatchNum, recordCache.getWatchNum())
                    .set(StreamRecord::getStarNum, recordCache.getStarNum())
                    .set(StreamRecord::getNewFansNum, recordCache.getNewFansNum());
            streamRecordService.lambdaUpdate()
                    .eq(StreamRecord::getStreamId, liveCache.getStreamId())
                    .set(StreamRecord::getStatus, LARY.Stream.Status.preDown);
            return 0L;
        });
        String duration = Duration.ofMillis(SystemKit.now() - room.getLastLogin()).toString();
        cacheComponent.setHash(kvBuilder.goLiveK(uid),"srsToken",token);
        cacheComponent.delete(kvBuilder.streamRecordK(uid, liveCache.getStreamId()));
        DownLiveVO downLiveVO = new DownLiveVO( recordCache.getWatchNum(), recordCache.getNewFansNum(), recordCache.getStarNum(), recordCache.getWatchFanNum(), duration,token,eventId);
        executor.execute(()->{
//            executeTurnOver(uid,liveCache.getStreamId());
        });
        messageService.send(new EndLiveDTO(uid,liveCache.getDanmakuId()));
        return BusinessKit.ok(downLiveVO);
    }

    @Override
    public ResponsePair<GoLiveVO> go(String ip, GoLiveDTO dto) {
        long uid = RequestContext.uid();
        String name = RequestContext.name();
        Map<Object, Object> liveInfo = cacheComponent.getHash(kvBuilder.goLiveK(uid));
        if (liveInfo != null) {
            return BusinessKit.fail("you already go live");
        }
        Map<Object, Object> map = cacheComponent.getHash(kvBuilder.deviceLoginK(uid, dto.getDeviceId()));
        if (map == null) {
            return BusinessKit.fail("access login device failed");
        }
        DeviceLoginCacheDTO deviceCache = DeviceLoginCacheDTO.of(map);
        if (deviceCache.getId() != dto.getDeviceId() ||
                deviceCache.getFlag() != dto.getDeviceFlag()) {
            return BusinessKit.fail("access device failed");
        }
        String remark = dto.getRemark();
        String sensitiveWord = SensitiveWordHelper.findFirst(remark);
        if(StringKit.isNotEmpty(sensitiveWord)){
            return BusinessKit.fail("sensitive word contains:"+sensitiveWord);
        }
        Room room = lambdaQuery()
                .eq(Room::getUid, uid)
                .eq(Room::getIsDelete,false)
                .one();
        if(room != null && room.getIsBlock()) {
            return BusinessKit.fail("room is blocked");
        }
        if (room == null) {
            if (StringKit.isEmpty(remark)) {
                remark = "新人开播，多多关注~";
            }
            room = new Room()
                    .setUid(uid)
                    .setIsHot(false)
                    .setScore(0L)
                    .setCategory(dto.getType())
                    .setLastLogin(SystemKit.now())
                    .setRemark(remark);
            save(room);
        }else {
            if (StringKit.isEmpty(remark)) {
                remark = room.getRemark();
            }
            lambdaUpdate()
                    .set(Room::getLastLogin, SystemKit.now())
                    .set(Room::getRemark, remark)
                    .set(Room::getIsHot,isHot(room))
                    .eq(Room::getUid, uid);
        }

//        LaryChannel laryChannel = new LaryChannel()
//                .setUid(uid)
//                .setChannelType(WK.CHANNEL.TYPE.DATA);
//        laryChannelService.save(laryChannel);
//        long danmakuId = laryChannel.getId();
        long danmakuId = 0;
        messageService.saveOrUpdateChannel(new CreateDanmakuChannelDTO(danmakuId));
        String identify = UUIDKit.uuidToShort(UUIDKit.getUUID());
        StreamRecord streamRecord = new StreamRecord()
                .setUid(uid)
                .setChannelId(danmakuId)
                .setIdentify(identify);
        streamRecordService.save(streamRecord);
        GoLiveEventDTO eventDTO = new GoLiveEventDTO(uid, dto.getDeviceId(), streamRecord.getStreamId(), danmakuId);
        long event = eventService.begin(eventDTO);
        if (room.getFollowNum() < 100) {
            ResponsePair<List<Integer>> pair = followService.getFollows(uid);
            if (pair.isFail()){
                return BusinessKit.fail(pair.getMsg());
            }
            List<Long> follows = null;
            if (CollectionKit.isNotEmpty(follows)) {
                messageService.send(new GoLiveNotifyDTO(uid,name,follows));
            }
        }
        String token = UUIDKit.getUUID();
        LiveCache cacheDTO = new LiveCache()
                .setIp(ip)
                .setDanmakuId(danmakuId)
                .setStreamId(streamRecord.getStreamId())
                .setIdentify(identify)
                .setSrsToken(token);
        cacheComponent.setHash(kvBuilder.goLiveK(uid),kvBuilder.goLiveV(cacheDTO));
        cacheComponent.setHash(kvBuilder.streamRecordK(uid, streamRecord.getStreamId()),kvBuilder.streamRecordV());
        GoLiveVO goLiveVO = new GoLiveVO(token,identify,event);
        return BusinessKit.ok(goLiveVO);
    }

    @Override
    public boolean isHot(Room room) {
        return  room.getFollowNum() > 1000;
    }

    public void executeTurnOver(long uid,int streamId) {
        ResponsePair<BigDecimal> pair = anchorFlowService.buildTurnover(uid, streamId);
        BigDecimal sum = pair.getData();
        streamRecordService.lambdaUpdate()
                .eq(StreamRecord::getStreamId, streamId)
                .set(StreamRecord::getGiftAmount, sum);
    }
}
