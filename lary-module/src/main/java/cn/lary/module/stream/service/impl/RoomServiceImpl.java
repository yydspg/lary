package cn.lary.module.stream.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.*;
import cn.lary.module.id.SystemClock;
import cn.lary.module.raffle.component.RaffleCacheComponent;
import cn.lary.module.redpacket.component.RedpacketCacheComponent;
import cn.lary.module.channel.dto.ChannelBuildDTO;
import cn.lary.module.channel.entity.LaryChannel;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.common.service.EventService;
import cn.lary.module.event.dto.GoLiveEventDTO;
import cn.lary.module.gift.service.AnchorFlowService;
import cn.lary.module.message.dto.stream.*;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.raffle.dto.RaffleEventCache;
import cn.lary.module.redpacket.dto.RedpacketEventCache;
import cn.lary.module.stream.component.LiveCacheComponent;
import cn.lary.module.stream.dto.GoLiveDTO;
import cn.lary.module.stream.dto.LiveCache;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.mapper.RoomMapper;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.stream.service.StreamTagService;
import cn.lary.module.stream.vo.DownLiveVO;
import cn.lary.module.stream.vo.GoLiveVO;
import cn.lary.module.stream.vo.JoinLiveVO;
import cn.lary.module.stream.vo.RoomVO;
import cn.lary.module.user.component.UserCache;
import cn.lary.module.user.component.UserCacheComponent;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private final LiveCacheComponent liveCacheComponent;
    private final UserCacheComponent userCacheComponent;
    private final RedpacketCacheComponent redpacketCacheComponent;
    private final RaffleCacheComponent raffleCacheComponent;

    private final EventService eventService;
    private final StreamRecordService streamRecordService;
    private final StreamTagService streamTagService;

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
    public ResponsePair<JoinLiveVO> join(long toUid,long sid, String ip) {
        long uid = RequestContext.uid();
        int flag = RequestContext.flag();
        String name = RequestContext.name();
        Follow relation = followService.lambdaQuery()
                .select(Follow::getUid,Follow::getToUid,Follow::getStatus)
                .eq(Follow::getUid, uid)
                .eq(Follow::getUid, toUid)
                .one();
        if(relation != null && relation.getStatus() == LARY.FOLLOW.STATUS.BLOCK){
            return BusinessKit.fail("u been blocked");
        }
        LiveCache live = liveCacheComponent.getLive(sid);
        if(live == null){
            return BusinessKit.fail("no live info");
        }
        long cid = live.getCid();
        String authToken = UUIDKit.getUUID();
        UserCache data = userCacheComponent.getData(uid,flag);
        data.setSid(live.getSid());
        userCacheComponent.setData(uid,flag,data);
        messageService.addSubscriber(new AddSubscribersDTO(live.getCid(), List.of(uid)));
        messageService.send(new JoinRoomDTO(uid,name, cid));
        String specify = live.getIdentify();
        if (StringKit.isEmpty(specify)) {
            return BusinessKit.fail("internal error");
        }
        RaffleEventCache raffle = raffleCacheComponent.getRaffle(live.getRid());
        RedpacketEventCache redPacket = redpacketCacheComponent.getRedpacket(live.getRid());
        JoinLiveVO vo = new JoinLiveVO()
                .setDanmakuId(cid)
                .setToken(authToken)
                .setSpecify(live.getIdentify())
                .setRaffle(raffle)
                .setRedPacket(redPacket);
        return BusinessKit.ok(vo);
    }

    @Override
    public ResponsePair<Void> leave() {
        long uid = RequestContext.uid();
        int flag = RequestContext.flag();
        UserCache data = userCacheComponent.getData(uid,flag);
        data.setSid(0L);
        userCacheComponent.setData(uid,flag,data);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<DownLiveVO> end() {
        long uid = RequestContext.uid();
        int flag = RequestContext.flag();
        UserCache data = userCacheComponent.getData(uid,flag);
        long rid = data.getSid();
        RaffleEventCache raffle = raffleCacheComponent.getRaffle(rid);
        if (raffle != null) {
            return BusinessKit.fail("exists unfinished raffle");
        }
        RedpacketEventCache redpacket = redpacketCacheComponent.getRedpacket(rid);
        if (redpacket != null) {
            return BusinessKit.fail("exists unfinished redpacket");
        }
        LiveCache live = liveCacheComponent.getLive(rid);
        if (live == null) {
            return BusinessKit.fail("no live info");
        }
        Room room = lambdaQuery()
                .select(Room::getLastLogin,Room::getRid)
                .eq(Room::getRid,rid)
                .eq(Room::getStatus,LARY.STREAM.STATUS.ING)
                .one();
        if (room == null) {
            log.error("internal error,no room found,sid:{}",rid);
            return BusinessKit.fail("room not exist");
        }
        StreamRecord record = streamRecordService.lambdaQuery()
                .select(StreamRecord::getRid)
                .eq(StreamRecord::getSid, live.getSid())
                .one();
        if (record == null) {
            log.error("internal error,no stream record found,sid:{}",rid);
            return BusinessKit.fail("no record");
        }
        String token = UUIDKit.getUUID();
        // TODO  : send mq
//        transactionTemplate.executeWithoutResult(status -> {
//            streamRecordService.lambdaUpdate()
//                    .set(StreamRecord::getStatus, LARY.STREAM.STATUS.DOWN)
//                    .set(StreamRecord::getWatchFanNum, recordCache.getWatchFanNum())
//                    .set(StreamRecord::getWatchNum, recordCache.getWatchNum())
//                    .set(StreamRecord::getStarNum, recordCache.getStarNum())
//                    .set(StreamRecord::getNewFansNum, recordCache.getNewFansNum())
//                    .eq(StreamRecord::getSid, live.getSid())
//                    .update();
//            return 0L;
//        });
        String duration = Duration.ofMillis(SystemKit.now() - room.getLastLogin()).toString();
//        cacheComponent.setHash(kvBuilder.goLiveK(uid),"srsToken",srsToken);
//        cacheComponent.delete(kvBuilder.streamRecordK(uid, liveCache.getSid()));
        DownLiveVO downLiveVO = null;
//                new DownLiveVO( recordCache.getWatchNum(), recordCache.getNewFansNum(), recordCache.getStarNum(), recordCache.getWatchFanNum(), duration,token,eventId);
        // send mq
        messageService.send(new EndLiveDTO(uid,live.getCid()));
        return BusinessKit.ok(downLiveVO);
    }

    @Override
    public ResponsePair<GoLiveVO> go(String ip, GoLiveDTO dto) {
        long uid = RequestContext.uid();
        int flag  = RequestContext.flag();
        if (!streamTagService.verify(dto.getTag())) {
            return BusinessKit.fail("tag invalid");
        }
        UserCache cache = userCacheComponent.getData(uid,flag);
        long rid = cache.getSid();
        LiveCache live = liveCacheComponent.getLive(cache.getSid());
        if (live != null ){
            return BusinessKit.fail("you already go live");
        }
        Room room = lambdaQuery()
                .select(Room::getTag, Room::getRid)
                .select(Room::getLastLogin, Room::getUid)
                .eq(Room::getRid, rid)
                .one();
        if (room != null && room.getUid() != uid) {
            return BusinessKit.fail("not support operation");
        }
        if (room != null && room.getStatus() == LARY.STREAM.STATUS.BLOCK) {
            return BusinessKit.fail("you already been blocked");
        }
        if (dto.getDid() != cache.getDid()) {
            return BusinessKit.fail("device id not match");
        }
        String remark = dto.getRemark();
        String sensitiveWord = SensitiveWordHelper.findFirst(remark);
        if(StringKit.isNotEmpty(sensitiveWord)){
            return BusinessKit.fail("sensitive word contains:"+sensitiveWord);
        }
        if (room == null && StringKit.isEmpty(remark)) {
            remark = "新人开播，多多关注~";
        }
        if (room != null && StringKit.isEmpty(remark)) {
            remark =  room.getRemark();
        }
        if (room == null) {
            room = new Room().setUid(uid)
                    .setStatus(LARY.STREAM.STATUS.ING)
                    .setScore(0L)
                    .setTag(dto.getTag())
                    .setLastLogin(SystemKit.now())
                    .setRemark(remark);
            save(room);
        }else {
            lambdaUpdate()
                    .set(Room::getLastLogin, SystemClock.now())
                    .set(Room::getRemark, remark)
//                    .set(Room::getLevel,isHot(room))
                    .eq(Room::getUid, uid)
                    .update();
        }
        LaryChannel channel = messageService.saveOrUpdateChannel(new ChannelBuildDTO()
                .setRid(rid)
                .setType(LARY.CHANNEL.TYPE.STREAM)
                .setUid(uid));
        long cid = channel.getId();
        String identify = UUIDKit.uuidToShort(UUIDKit.getUUID());
        StreamRecord streamRecord = new StreamRecord()
                .setUid(uid)
                .setCid(cid)
                .setIdentify(identify);
        streamRecordService.build(streamRecord);
        long sid = streamRecord.getSid();
        GoLiveEventDTO eventDTO = new GoLiveEventDTO()
                .setUid(uid)
                .setDid(dto.getDid())
                .setSid(sid)
                .setCid(cid);
        long event = eventService.begin(eventDTO);

        //change here
//        if (room.getFollowNum() < 100) {
//            ResponsePair<List<Long>> pair = followService.getFollows(uid);
//            if (pair.isFail()){
//                return BusinessKit.fail(pair.getMsg());
//            }
//            List<Long> follows = null;
//            if (CollectionKit.isNotEmpty(follows)) {
//                messageService.send(new GoLiveNotifyDTO(uid,name,follows));
//            }
//        }
        String token = UUIDKit.getUUID();
        LiveCache cacheDTO = new LiveCache()
                .setIp(ip)
                .setCid(cid)
                .setSid(sid)
                .setIdentify(identify)
                .setSrsToken(token);
        liveCacheComponent.setLive(rid, cacheDTO);
        GoLiveVO goLiveVO = new GoLiveVO(token,identify,event);
        return BusinessKit.ok(goLiveVO);
    }

    @Override
    public ResponsePair<List<RoomVO>> show() {
        return null;
    }
//    // 获取 level
//    @Override
//    public boolean isHot(Room room) {
//        return  room.getFollowNum() > 1000;
//    }

    public void executeTurnOver(long uid,int streamId) {
        ResponsePair<BigDecimal> pair = anchorFlowService.buildTurnover(uid, streamId);
        BigDecimal sum = pair.getData();
        streamRecordService.lambdaUpdate()
                .eq(StreamRecord::getSid, streamId)
                .set(StreamRecord::getGiftAmount, sum);
    }

    /**
     * 获取直播计算直播等级
     * @param room {@link Room}
     * @return level
     */
    private int level(Room room) {
        return 0;
    }
}
