package cn.lary.module.stream.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.*;
import cn.lary.module.app.entity.LaryChannel;
import cn.lary.module.app.service.EventService;
import cn.lary.module.app.service.LaryChannelService;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.event.dto.DownLiveEventDTO;
import cn.lary.module.event.dto.GoLiveEventDTO;
import cn.lary.module.gift.entity.AnchorTurnover;
import cn.lary.module.gift.service.AnchorTurnoverService;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.stream.dto.GoLiveDTO;
import cn.lary.module.stream.dto.JoinLiveCacheDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.dto.StreamRecordCacheDTO;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.entity.GiftBuyChannel;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.stream.service.GiftBuyChannelService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.stream.vo.DownLiveVO;
import cn.lary.module.stream.vo.GoLiveVO;
import cn.lary.module.stream.vo.JoinLiveVO;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.dto.channel.ChannelCreateDTO;
import cn.lary.pkg.wk.dto.channel.SubscribersAddDTO;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.entity.core.WK;
import cn.lary.pkg.wk.entity.core.WKChannel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomBizExecute {

    private final DeviceService deviceService;
    private final RoomService roomService;
    private final UserService userService;
    private final EventService eventService;
    private final FollowService followService;
    private final GiftBuyChannelService giftBuyChannelService;
    private final StreamRecordService streamRecordService;
    private final GiftOrderService giftOrderService;
    private final KVBuilder kvBuilder;
    private final RedisCache redisCache;
    private final AnchorTurnoverService anchorTurnoverService;
    private final LaryChannelService laryChannelService;
    //external
    private final WKChannelService wkChannelService;
    private final WKMessageService wkMessageService;
    private final WKUserService wkUserService;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            15, 20, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

        }
    });

    /**
     * 加入直播间
     * @param uid u
     * @param toUid anchor uid
     * @param ip ip
     * @return {@link JoinLiveVO}
     */
    // TODO  :  加入直播间，和结束直播，都需要针对直播抽奖和红包做处理
    public ResPair<JoinLiveVO> join(int uid,String uidName,int toUid,String ip){
        // check user status
        // check whether toUid block uid
        Follow relation = followService.getOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getUid, toUid).eq(Follow::getIsDelete, false));
        boolean isFan = false;
        if(relation != null && relation.getIsBlock()){
            return BizKit.fail("你已被" + uidName+ "拉黑");
        }
        if (relation == null || !relation.getIsUnfollow()) {
            isFan = true;
        }
        // check room status
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid).eq(Room::getIsAlive, true).eq(Room::getIsDelete,false), false);
        if (room == null || room.getIsBlock()) {
            return BizKit.fail("直播间违规了，在看看吧");
        }
        Map<Object,Object> args = redisCache.getHash(kvBuilder.goLiveK(toUid));
        if (args == null) {
            return BizKit.fail("no live info");
        }
        //prepare live info
        LiveCacheDTO liveCache = LiveCacheDTO.of(args);
        StreamRecord stream = streamRecordService.getOne(new LambdaQueryWrapper<StreamRecord>().eq(StreamRecord::getStreamId, liveCache.getStreamId()).eq(StreamRecord::getIsDelete, false));
        if (stream == null || stream.getStatus() != Lary.Stream.Status.up) {
            return BizKit.fail("stream status error");
        }
        redisCache.incrHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()),"watchNum");
        if (isFan) {
            redisCache.incrHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()),"watchFanNum");
        }
        String authToken = UUIDKit.getUUID();
        // 30 minutes
        long expire = 30;
        JoinLiveCacheDTO dto = new JoinLiveCacheDTO()
            .setIp(ip)
            .setStreamId(liveCache.getStreamId())
            .setSrsToken(authToken)
            .setSrsStreamId(liveCache.getSrsStreamId())
            .setStream(liveCache.getStream());
        redisCache.setHash(kvBuilder.joinLiveK(uid),kvBuilder.joinLiveV(dto),expire, TimeUnit.MINUTES);

        // add user to wk temp channel
        SubscribersAddDTO addDTO = new SubscribersAddDTO(liveCache.getWkChannelId(), WK.ChannelType.stream,0, 0, List.of(uid));
        Response<Void> addSubscriberVO = wkChannelService.addSubscribers(addDTO);
        if (!addSubscriberVO.isSuccessful()) {
            log.error("wk channel add subscribers error:{}", addSubscriberVO.errorBody());
            return BizKit.fail("wk channel add subscribers error");
        }
        // send user into room  message
        String content = uidName + "来了";
        MessageSendDTO sendDTO = new MessageSendDTO().setHeader(new MessageHeader().setNoPersist(1));
        sendDTO.setFromUID(uid).setPayload(content.getBytes(StandardCharsets.UTF_8)).setChannelID(liveCache.getWkChannelId()).setChannelType(WK.ChannelType.stream);
        Response<Void> sendVO = wkMessageService.send(sendDTO);
        if (!sendVO.isSuccessful()) {
            log.error("wk message send error:{}", sendVO.errorBody());
            return BizKit.fail("wk message send error");
        }
        // build response vo
        String specify = stream.getIdentify();
        if (StringKit.isEmpty(specify)) {
            return BizKit.fail("stream url is empty");
        }
        JoinLiveVO joinLiveVO = new JoinLiveVO(liveCache.getWkChannelId(),specify,authToken);
        return BizKit.ok(joinLiveVO);
    }

    /**
     * 加入直播间
     * @param uid uer id
     * @param uidName user name
     * @param ip ip
     * @param req {@link GoLiveDTO}
     * @return {@link GoLiveVO}
     */
    public ResPair<GoLiveVO> go(Integer uid,String uidName,String ip, GoLiveDTO req) {

        // check whether living now
        Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.goLiveK(uid));
        if (liveInfo != null) {
            return BizKit.fail("you already go live");
        }
        // check device status
        Device device = deviceService.queryDevice(uid, req.getDeviceId());
        if (device == null) {
            return BizKit.fail("device status error");
        }
        //check sensitive word
        String remark = req.getRemark();
        String sensitiveWord = SensitiveWordHelper.findFirst(remark);
        if(StringKit.isNotEmpty(sensitiveWord)){
            return BizKit.fail("sensitive word contains:"+sensitiveWord);
        }
        // check if is first start stream
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid).eq(Room::getIsAlive, true).eq(Room::getIsDelete,false), false);
        if(room != null && room.getIsBlock()) {
            return BizKit.fail("room is blocked");
        }
        if (room == null) {
            if (StringKit.isEmpty(remark)) {
                remark = "新人开播，多多关注~";
            }
            room = new Room().setUid(uid)
                    .setIsAlive(true)
                    .setIsHot(false)
                    .setScore(0L)
                    .setStreamTypeId(req.getTypeId())
                    .setLastLogin(SystemKit.now())
                    .setRemark(remark);
            roomService.save(room);
        }else {
            if (StringKit.isEmpty(remark)) {
                remark = room.getRemark();
            }
            Room updateRecord = new Room().setId(room.getId())
                    .setUid(uid)
                    .setLastLogin(SystemKit.now())
                    .setRemark(remark)
                    .setIsAlive(true);
            boolean isHot = room.getFollowNum() > 1000;
            updateRecord.setIsHot(isHot);
            roomService.updateById(updateRecord);
        }
        // build danmaku channel

//        if (wkChannel == null) {
//            return BizKit.fail("danmaku channel not available");
//        }
        LaryChannel laryChannel = new LaryChannel().setUid(uid)
                .setChannelType(WK.ChannelType.data);
        laryChannelService.save(laryChannel);
        int danmakuId = laryChannel.getId();
        WKChannel wkChannel = new WKChannel().setChannelId(laryChannel.getId())
                .setChannelType(WK.ChannelType.stream);
        GiftBuyChannel giftBuyChannel = new GiftBuyChannel().setAnchorId(uid);
        giftBuyChannelService.save(giftBuyChannel);
        StreamRecord streamRecord = new StreamRecord()
                .setUid(uid)
                .setChannelId(danmakuId)
                .setGiftBuyRecordId(giftBuyChannel.getChannelId());
        String stream = UUIDKit.uuidToShort(UUIDKit.getUUID());
        streamRecord.setIdentify(stream);
        streamRecordService.save(streamRecord);
        // start event
        GoLiveEventDTO eventDTO = new GoLiveEventDTO(uid, device.getId(), streamRecord.getStreamId(), danmakuId, giftBuyChannel.getChannelId());
        int event = eventService.begin(eventDTO.of());
        if (room.getFollowNum() < 100) {
            List<String> follows = followService.getFollows(uid);
            if (CollectionKit.isNotEmpty(follows)) {
                    //send message to wk
                    // build temporary channel to send start alert
                    ChannelCreateDTO createTempDTO = new ChannelCreateDTO(String.valueOf(danmakuId),WK.ChannelType.data,0,0,follows);
                    wkChannelService.createOrUpdate(createTempDTO);
                    String content = uidName + "开播了，快来围观";
                    MessageSendDTO messageSendDTO = new MessageSendDTO()
                            .setHeader(new MessageHeader().setNoPersist(1).setRedDot(1))
                            .setChannelID(danmakuId)
                            .setChannelType(WK.ChannelType.data)
                            .setFromUID(uid)
                            .setPayload(content.getBytes(StandardCharsets.UTF_8));
                    wkMessageService.send(messageSendDTO);
                    // send mq to delay deletion temp channel
                    // TODO  :  here need to achieve rocket mq
                }
        }
        // put it into redis
        redisCache.setHash(kvBuilder.streamRecordK(uid, streamRecord.getStreamId()),kvBuilder.streamRecordV());
        // now gift buy channel , stream channel , message delivery channel id can set to redis
        String token = UUIDKit.getUUID();
        LiveCacheDTO dto = new LiveCacheDTO()
                .setIp(ip)
                .setGiftBuyChannelId(giftBuyChannel.getChannelId())
                .setWkChannelId(danmakuId)
                .setStreamId(streamRecord.getStreamId())
                .setStream(stream)
                .setSrsToken(token);
        redisCache.setHash(kvBuilder.goLiveK(uid),kvBuilder.goLiveV(dto));
        // build return vo
        GoLiveVO goLiveVO = new GoLiveVO(token,stream,event);
        return BizKit.ok(goLiveVO);
    }

    /**
     * 结束直播
     * @param uid user id
     * @param uidName user name
     * @return {@link DownLiveVO}
     */
    public ResPair<DownLiveVO> end(Integer uid,String uidName) {

        // check room status
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>()
                .eq(Room::getUid, uid)
                .eq(Room::getIsAlive, true)
                .eq(Room::getIsBlock,false), false);
        if (room == null) {
            return BizKit.fail("room not exist");
        }
        Map<Object, Object> coll = redisCache.getHash(kvBuilder.goLiveK(uid));
        if (coll == null) {
            return BizKit.fail("no live info");
        }
        LiveCacheDTO liveCache = LiveCacheDTO.of(coll);
        //start event
        DownLiveEventDTO downLiveEventDTO = new DownLiveEventDTO(uid, liveCache.getStreamId(), liveCache.getWkChannelId(), liveCache.getGiftBuyChannelId());
        int event = eventService.begin(downLiveEventDTO.of());
        // get stream record cache
        Map<Object, Object> record = redisCache.getHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()));
        if (record == null) {
            return BizKit.fail("stream record not exist");
        }
        StreamRecordCacheDTO recordCache = StreamRecordCacheDTO.of(record);

        String token = UUIDKit.getUUID();
        // update down live token
        redisCache.setHash(kvBuilder.goLiveK(uid),"srsToken",token);
        // update room status
        roomService.update(new LambdaUpdateWrapper<Room>().set(Room::getIsAlive,false).eq(Room::getUid,uid));
        streamRecordService.lambdaUpdate()
                .eq(StreamRecord::getStreamId,liveCache.getStreamId())
                .set(StreamRecord::getStatus, Lary.Stream.Status.preDown);

        executor.execute(()->{
            doAnchorTurnOver(uid,liveCache.getStreamId());
        });
        redisCache.del(kvBuilder.streamRecordK(uid, liveCache.getStreamId()));
        // send close live info to wk channel
        String content =   "主播已经离开，稍后再来哦";
        MessageSendDTO sendDTO = new MessageSendDTO().setHeader(new MessageHeader().setNoPersist(1));
        sendDTO.setFromUID(uid).setPayload(content.getBytes(StandardCharsets.UTF_8)).setChannelID(liveCache.getWkChannelId()).setChannelType(WK.ChannelType.data);
        wkMessageService.send(sendDTO);
        //update stream record
        streamRecordService.update(new LambdaUpdateWrapper<StreamRecord>()
                .eq(StreamRecord::getStreamId,liveCache.getStreamId())
                .set(StreamRecord::getStatus, Lary.Stream.Status.down)
                .set(StreamRecord::getWatchFanNum,recordCache.getWatchFanNum())
                .set(StreamRecord::getWatchNum,recordCache.getWatchNum())
                .set(StreamRecord::getStarNum,recordCache.getStarNum())
                .set(StreamRecord::getNewFansNum,recordCache.getNewFansNum()));
        String duration = Duration.ofMillis(SystemKit.now() - room.getLastLogin()).toString();
        DownLiveVO downLiveVO = new DownLiveVO( recordCache.getWatchNum(), recordCache.getNewFansNum(), recordCache.getStarNum(), recordCache.getWatchFanNum(), duration,token,event);
        return BizKit.ok(downLiveVO);
    }

    /**
     * 离开直播间
     * @param uid user id
     * @return ok
     */
    public ResPair<Void> leave(int uid) {
        Map<Object, Object> map = redisCache.getHash(kvBuilder.joinLiveK(uid));
        if (map == null) {
            return BizKit.fail("no join live info");
        }
        redisCache.del(kvBuilder.joinLiveK(uid));
        return BizKit.ok();
    }

    public void doAnchorTurnOver(int uid,int streamId) {
        StreamRecord record = streamRecordService.getOne(new LambdaQueryWrapper<StreamRecord>()
                .eq(StreamRecord::getStreamId, streamId));

        long sum = 0L;
        List<Long> data = anchorTurnoverService.listObjs(new LambdaQueryWrapper<AnchorTurnover>()
                .select(AnchorTurnover::getIncome)
                .eq(AnchorTurnover::getAnchorId, record.getUid())
                .eq(AnchorTurnover::getStreamId, streamId));
        if (CollectionKit.isNotEmpty(data)) {
            for (Long t : data) {
                sum += t;
            }
        }
        streamRecordService.update(new LambdaUpdateWrapper<StreamRecord>()
                .eq(StreamRecord::getStreamId, streamId)
                .eq(StreamRecord::getUid, uid)
                .set(StreamRecord::getStatus, sum));
    }
    /**
     * block接口,这里不打算实现这个逻辑的原因是，如果要手动下线用户
     * 就要维护srs节点，所以现在的情况是在 mq检查连接的时候，判断用户关系
     * todo : 好像还是存在直接封掉用户问题，必须要将控制权反转，所以必须要维护这个关系
     * @param toUid 被封禁用户
     * @return ok
     */
    @Deprecated
    public ResPair<Void> block(int uid,int toUid) {
        throw new RuntimeException("");
    }
}



