package cn.lary.module.stream.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.*;
import cn.lary.module.app.entity.LaryChannel;
import cn.lary.module.app.service.EventService;
import cn.lary.module.app.service.LaryChannelService;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.dto.DownLiveEventDTO;
import cn.lary.module.event.dto.GoLiveEventDTO;
import cn.lary.module.gift.service.AnchorTurnoverService;
import cn.lary.module.message.dto.stream.*;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.stream.dto.*;
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
import cn.lary.module.user.dto.DeviceLoginCacheDTO;
import cn.lary.pkg.wk.constant.WK;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final RedisCache redisCache;
    private final EventService eventService;
    private final StreamRecordService streamRecordService;
    private final LaryChannelService laryChannelService;
    private final FollowService followService;
    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;
    private final AnchorTurnoverService anchorTurnoverService;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            15, 20, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

        }
    });

    @Override
    public ResponsePair<JoinLiveVO> join(int toUid, String ip) {
        int uid = RequestContext.getLoginUID();
        String name = RequestContext.getLoginName();
        Follow relation = followService.lambdaQuery()
                .select(Follow::getIsBlock)
                .eq(Follow::getUid, uid)
                .eq(Follow::getUid, toUid)
                .eq(Follow::getIsDelete,false)
                .one();
        boolean isFan = false;
        if(relation != null && relation.getIsBlock()){
            return BusinessKit.fail("你已被拉黑");
        }
        if (relation == null || !relation.getIsUnfollow()) {
            isFan = true;
        }
        Map<Object,Object> args = redisCache.getHash(kvBuilder.goLiveK(toUid));
        if (args == null) {
            return BusinessKit.fail("no live info");
        }
        LiveCacheDTO liveCache = LiveCacheDTO.of(args);
        args = redisCache.getHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()));
        if (args == null) {
            return BusinessKit.fail("no stream record");
        }
        redisCache.incrHash(kvBuilder.streamRecordK(toUid, liveCache.getStreamId()),"watchNum");
        if (isFan) {
            redisCache.incrHash(kvBuilder.streamRecordK(toUid, liveCache.getStreamId()),"watchFanNum");
        }
        String authToken = UUIDKit.getUUID();
        // 30 minutes
        long expire = 30;
        JoinLiveCacheDTO dto = new JoinLiveCacheDTO()
                .setIp(ip)
                .setStreamId(liveCache.getStreamId())
                .setSrsToken(authToken)
                .setSrsStreamId(liveCache.getSrsStreamId())
                .setIdentify(liveCache.getIdentify());
        redisCache.setHash(kvBuilder.joinLiveK(uid),kvBuilder.joinLiveV(dto),expire, TimeUnit.MINUTES);
        messageService.addSubscriber(new AddSubscribersDTO(liveCache.getDanmakuId(), List.of(uid)));
        messageService.send(new JoinRoomDTO(uid,name,liveCache.getDanmakuId()));
        String specify = liveCache.getIdentify();
        if (StringKit.isEmpty(specify)) {
            return BusinessKit.fail("stream url is empty");
        }
        RaffleCacheDTO raffle = null;
        RedPacketCacheDTO redPacket = null;
        Map<Object, Object> map = redisCache.getHash(kvBuilder.raffleK(toUid));
        if (map != null) {
            raffle = RaffleCacheDTO.of(map);
        }
        map = redisCache.getHash(kvBuilder.redPacketK(toUid));
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
        int uid = RequestContext.getLoginUID();
        Map<Object, Object> map = redisCache.getHash(kvBuilder.joinLiveK(uid));
        if (map == null) {
            return BusinessKit.fail("no join live info");
        }
        redisCache.delete(kvBuilder.joinLiveK(uid));
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<DownLiveVO> end() {
        int uid = RequestContext.getLoginUID();
        Map<Object, Object> coll = null;
        coll = redisCache.getHash(kvBuilder.raffleK(uid));
        if (coll != null) {
            return BusinessKit.fail("存在未完成的抽奖活动");
        }
        coll = redisCache.getHash(kvBuilder.redPacketK(uid));
        if (coll != null) {
            return BusinessKit.fail("存在未结束的红包活动");
        }
        coll = redisCache.getHash(kvBuilder.goLiveK(uid));
        if (coll == null) {
            return BusinessKit.fail("no live info");
        }
        LiveCacheDTO liveCache = LiveCacheDTO.of(coll);
        Map<Object, Object> record = redisCache.getHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()));
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
        DownLiveEventDTO downLiveEventDTO = new DownLiveEventDTO(uid, liveCache.getStreamId(), liveCache.getDanmakuId());
        Integer eventId = transactionTemplate.execute(status -> {
            int event = eventService.begin(downLiveEventDTO.of());
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
            return event;
        });
        String duration = Duration.ofMillis(SystemKit.now() - room.getLastLogin()).toString();
        redisCache.setHash(kvBuilder.goLiveK(uid),"srsToken",token);
        redisCache.delete(kvBuilder.streamRecordK(uid, liveCache.getStreamId()));
        DownLiveVO downLiveVO = new DownLiveVO( recordCache.getWatchNum(), recordCache.getNewFansNum(), recordCache.getStarNum(), recordCache.getWatchFanNum(), duration,token,eventId);
        executor.execute(()->{
            executeTurnOver(uid,liveCache.getStreamId());
        });
        messageService.send(new EndLiveDTO(uid,liveCache.getDanmakuId()));
        return BusinessKit.ok(downLiveVO);
    }

    @Override
    public ResponsePair<GoLiveVO> go(String ip, GoLiveDTO dto) {
        int uid = RequestContext.getLoginUID();
        String name = RequestContext.getLoginName();
        Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.goLiveK(uid));
        if (liveInfo != null) {
            return BusinessKit.fail("you already go live");
        }
        Map<Object, Object> map = redisCache.getHash(kvBuilder.deviceLoginK(uid, dto.getDeviceId()));
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

        LaryChannel laryChannel = new LaryChannel()
                .setUid(uid)
                .setChannelType(WK.ChannelType.data);
        laryChannelService.save(laryChannel);
        int danmakuId = laryChannel.getId();
        messageService.saveOrUpdateChannel(new CreateDanmakuChannelDTO(danmakuId));
        String identify = UUIDKit.uuidToShort(UUIDKit.getUUID());
        StreamRecord streamRecord = new StreamRecord()
                .setUid(uid)
                .setChannelId(danmakuId)
                .setIdentify(identify);
        streamRecordService.save(streamRecord);
        GoLiveEventDTO eventDTO = new GoLiveEventDTO(uid, dto.getDeviceId(), streamRecord.getStreamId(), danmakuId);
        int event = eventService.begin(eventDTO.of());
        if (room.getFollowNum() < 100) {
            ResponsePair<List<Integer>> pair = followService.getFollows(uid);
            if (pair.isFail()){
                return BusinessKit.fail(pair.getMsg());
            }
            List<Integer> follows = null;
            if (CollectionKit.isNotEmpty(follows)) {
                messageService.send(new GoLiveNotifyDTO(uid,name,follows));
            }
        }
        String token = UUIDKit.getUUID();
        LiveCacheDTO cacheDTO = new LiveCacheDTO()
                .setIp(ip)
                .setDanmakuId(danmakuId)
                .setStreamId(streamRecord.getStreamId())
                .setIdentify(identify)
                .setSrsToken(token);
        redisCache.setHash(kvBuilder.goLiveK(uid),kvBuilder.goLiveV(cacheDTO));
        redisCache.setHash(kvBuilder.streamRecordK(uid, streamRecord.getStreamId()),kvBuilder.streamRecordV());
        GoLiveVO goLiveVO = new GoLiveVO(token,identify,event);
        return BusinessKit.ok(goLiveVO);
    }

    @Override
    public boolean isHot(Room room) {
        return  room.getFollowNum() > 1000;
    }

    public void executeTurnOver(int uid,int streamId) {
        ResponsePair<Long> pair = anchorTurnoverService.buildTurnover(uid, streamId);
        Long sum = pair.getData();
        streamRecordService.lambdaUpdate()
                .eq(StreamRecord::getStreamId, streamId)
                .eq(StreamRecord::getUid, uid)
                .set(StreamRecord::getStatus, sum);
    }
}
