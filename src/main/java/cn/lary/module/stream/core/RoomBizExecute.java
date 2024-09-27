package cn.lary.module.stream.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.*;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.event.dto.DownLiveEventDTO;
import cn.lary.module.event.dto.GoLiveEventDTO;
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
import cn.lary.module.user.vo.UserBaseVO;
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
    private final KVBuilder kvBuilder;
    private final RedisCache redisCache;
    //external
    private final WKChannelService wkChannelService;
    private final WKMessageService wkMessageService;
    private final WKUserService wkUserService;

    /**
     * 加入直播间
     * @param uid u
     * @param toUid anchor uid
     * @param ip ip
     * @return {@link JoinLiveVO}
     */
    public ResPair<JoinLiveVO> join(int uid,String uidName,int toUid,String ip){
        // check user status
//        UserBaseVO userBase = userService.queryBase(uid);
//        if(userBase == null){
//            return BizKit.fail("user status error");
//        }
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
        // check live status
        Map<Object,Object> args = redisCache.getHash(kvBuilder.goLiveK(toUid));
        if (args == null) {
            return BizKit.fail("no live info");
        }
        //prepare live info
        LiveCacheDTO liveCache = LiveCacheDTO.of(args);
        // prepare pull stream province
        // check stream info
        StreamRecord stream = streamRecordService.getOne(new LambdaQueryWrapper<StreamRecord>().eq(StreamRecord::getStreamId, liveCache.getStreamId()).eq(StreamRecord::getIsDelete, false));
        if (stream == null || stream.getStatus() != Lary.Stream.Status.up) {
            return BizKit.fail("stream status error");
        }
        // incr stream watch num
        redisCache.incrHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()),"watchNum");
        if (isFan) {
            redisCache.incrHash(kvBuilder.streamRecordK(uid, liveCache.getStreamId()),"watchFanNum");
        }
        // add joinLive cache
        String authToken = UUIDKit.getUUID();
        // 30 minutes
        long expire = 30;
        JoinLiveCacheDTO dto = new JoinLiveCacheDTO();
        dto.setIp(ip);
        dto.setStreamId(liveCache.getStreamId());
        dto.setSrsToken(authToken);
        dto.setSrsStreamId(liveCache.getSrsStreamId());
        dto.setStream(liveCache.getStream());
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
        // check user status
//        UserBaseVO userBase = userService.queryBase(uid);
//        if(userBase == null){
//            return BizKit.fail("user status error");
//        }
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
            return BizKit.fail("sensitive word:"+sensitiveWord);
        }
        // check if is first start stream
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid).eq(Room::getIsAlive, true).eq(Room::getIsDelete,false), false);
        if(room != null && room.getIsBlock()) {
            return BizKit.fail("room is block");
        }
        if (room == null) {
            // check remark
            if (StringKit.isEmpty(remark)) {
                remark = "新人开播，多多关注~";
            }
            // first time to start stream
            room = new Room().setUid(uid).setIsAlive(true).setIsHot(false).setScore(0L)
                    .setStreamTypeId(req.getTypeId()).setLastLogin(SystemKit.now()).setRemark(remark);
            roomService.save(room);
        }else {
            if (StringKit.isEmpty(remark)) {
                remark = room.getRemark();
            }
            // update room
            Room updateRecord = new Room().setId(room.getId()).setUid(uid).setLastLogin(SystemKit.now())
                    .setRemark(remark).setIsAlive(true);
            boolean isHot = room.getFollowNum() > 1000;
            updateRecord.setIsHot(isHot);
            roomService.updateById(updateRecord);
        }
        // build danmaku channel
        WKChannel wkChannel = null;
//        if (wkChannel == null) {
//            return BizKit.fail("danmaku channel not available");
//        }

        // prepare gift send channel
        GiftBuyChannel giftBuyChannel = new GiftBuyChannel().setAnchorId(uid);
        giftBuyChannelService.save(giftBuyChannel);
        // build stream record
        StreamRecord streamRecord = new StreamRecord()
                .setUid(uid)
                .setChannelId(wkChannel.getChannelId())
                .setGiftBuyRecordId(giftBuyChannel.getChannelId());
        // set stream url
        String stream = UUIDKit.uuidToShort(UUIDKit.getUUID());
        streamRecord.setIdentify(stream);
        streamRecordService.save(streamRecord);
        // start event
        GoLiveEventDTO eventDTO = new GoLiveEventDTO(uid, device.getDeviceId(), streamRecord.getStreamId(), wkChannel.getChannelId(), giftBuyChannel.getChannelId());
        int event = eventService.begin(eventDTO.of());
        if (room.getFollowNum() < 100) {
            List<String> follows = followService.getFollows(uid);
            if (CollectionKit.isNotEmpty(follows)) {
                    //send message to wk
                    // build temporary channel to send start alert
                // TODO  :  弹幕channel管理实现
//                    ChannelCreateDTO createTempDTO = new ChannelCreateDTO(tempChannelId,WK.ChannelType.data,0,0,follows);
//                    wkChannelService.createOrUpdate(createTempDTO);
//                    String content = uidName + "开播了，快来围观";
//                    MessageSendDTO messageSendDTO = new MessageSendDTO()
//                            .setHeader(new MessageHeader().setNoPersist(1).setRedDot(1))
//                            .setChanelID(tempChannelId)
//                            .setChannelType(WK.ChannelType.data)
//                            .setFromUID(uid)
//                            .setPayload(content.getBytes(StandardCharsets.UTF_8));
//                    wkMessageService.send(messageSendDTO);
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
                .setWkChannelId(wkChannel.getChannelId())
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
        // check user status
//        UserBaseVO userBase = userService.queryBase(uid);
//        if(userBase == null){
//            return BizKit.fail("user status error");
//        }
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
        streamRecordService.update(new LambdaUpdateWrapper<StreamRecord>().eq(StreamRecord::getStreamId,liveCache.getStreamId()).set(StreamRecord::getStatus, Lary.Stream.Status.preDown));

        //async  collect gift cost
        //build return vo
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



