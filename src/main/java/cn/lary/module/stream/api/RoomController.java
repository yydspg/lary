package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.*;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.danmaku.service.DanmakuService;
import cn.lary.module.gift.service.GiftBuyRecordService;
import cn.lary.module.stream.dto.req.StartStreamReq;
import cn.lary.module.stream.dto.res.JoinStreamRes;
import cn.lary.module.stream.entity.GiftBuyChannel;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.service.FollowApplyService;
import cn.lary.module.stream.service.GiftBuyChannelService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.srs.config.SrsConfig;
import cn.lary.pkg.srs.service.ProvinceService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.entity.Request.channel.ChannelCreateReq;
import cn.lary.pkg.wk.entity.Request.channel.SubscribersAddReq;
import cn.lary.pkg.wk.entity.Request.message.MessageHeader;
import cn.lary.pkg.wk.entity.Request.message.MessageSendReq;
import cn.lary.pkg.wk.entity.Response.user.OnlineStatus;
import cn.lary.pkg.wk.entity.core.Channel;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@Slf4j
@RestController
@RequestMapping("/v1/stream/room")
@RequiredArgsConstructor
public class RoomController {

    private final DeviceService deviceService;
    private final RoomService roomService;
    private final DanmakuService danmakuService;
    private final SrsConfig srsConfig;
    private final ProvinceService provinceService;
    private final EventService eventService;
    private final FollowApplyService followApplyService;
    private final GiftBuyChannelService giftBuyChannelService;
    private final StreamRecordService streamRecordService;
    private final GiftBuyRecordService giftBuyRecordService;
    private final KVBuilder kvBuilder;

    private final WKChannelService wkChannelService;
    private final WKMessageService wkMessageService;
    private final WKUserService wkUserService;
    private final RedisCache redisCache;

    /**
     * 开启直播
     * @param req {@link StartStreamReq} 直播请求参数
     * @return uid,deviceId,streamId,channelId,giftBuyRecordId
     * @code
     * {
     *     "uid" : "test",
     *     "deviceId" : "test",
     *     "streamId" : "test",
     *     "channelId" : "test",
     *     "giftBuyRecord“ ：”test"
     * }
     *
     */
    @PostMapping("/start")
    public SingleResponse start(@RequestBody @Valid StartStreamReq req) {

        String uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        String deviceId = req.getDeviceId();
        String province  = req.getProvince();
        // check whether living now
        Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.buildGoLiveKey(uid));
        if (liveInfo != null) {
            return ResKit.fail("you already go live");
        }
        //query device
        Device device = deviceService.queryDevice(deviceId, uid);
        if (device == null) {
            return ResKit.fail("no such device");
        }
        // check if is first start stream
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid));

        if (room == null) {
            // first time to start stream
            room = new Room().setUid(uid).setIsAlive(true).setIsHot(false)
                    .setScore(0L);
            roomService.save(room);
        } else {
            if (room.getIsBlock()) {
                return ResKit.fail("room is block");
            }
            if (room.getIsDelete()) {
                return ResKit.fail("data unavailable");
            }
        }
        // build danmaku channel
        Channel danmakuChannel = danmakuService.getDanmakuChannel(uid);
        if (danmakuChannel == null) {
            return ResKit.fail("danmaku channel not available");
        }

        // build stream server addr
        String nearbyProvince = null;
        if (!provinceService.exist(province)) {
            nearbyProvince = provinceService.getNearbyProvince(province);
        }
        String server = null;
        // no exists province
        if (nearbyProvince != null) {
            server = srsConfig.getEdgeServerRandom(nearbyProvince);
        }else {
            server = srsConfig.getEdgeServerRandom(province);
        }
        // prepare gift send channel
        String giftBuyChannelId = UUIDKit.getUUID();
        GiftBuyChannel giftBuyChannel = new GiftBuyChannel().setChannelId(giftBuyChannelId).setCreateBy(uid);
        giftBuyChannelService.save(giftBuyChannel);
        // build stream record
        String streamId = UUIDKit.getUUID();
        StreamRecord streamRecord = new StreamRecord().setUid(uid).setStreamId(streamId).setChannelId(danmakuChannel.getChanelID()).setGiftBuyRecordId(giftBuyChannelId);
        // set stream url
        String specify = UUIDKit.uuidToShortString(uid);
        String serverAddr = server + "/" + specify;
        streamRecord.setStreamUrl(serverAddr);
        streamRecord.setIdentify(specify);
        streamRecordService.save(streamRecord);
        // put it into redis
        redisCache.setHash( kvBuilder.buildStreamRecordKey(uid,streamId),kvBuilder.buildStreamRecordValue());
        // start event
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("deviceId", deviceId);
        map.put("streamId", streamId);
        map.put("channelId", danmakuChannel.getChanelID());
        map.put("giftBuyChannelId", giftBuyChannelId);
        EventData data = new EventData().setEvent(Lary.Event.streamStart).setType(Lary.EventType.message).setData(JSONKit.toJSON(map));

        int eventId = eventService.begin(data);
        List<String> follows = followApplyService.getFollows(uid);
        if (CollectionKit.isNotEmpty(follows)) {
            //send message to wk
            // build temporary channel to send start alert
            String tempChannelId = UUIDKit.getUUID();
            ChannelCreateReq createReq = new ChannelCreateReq().setSubscribers(follows);
            createReq.setChanelID(tempChannelId).setChannelType(WK.ChannelType.data);
            wkChannelService.createOrUpdate(createReq);
            MessageSendReq messageSendReq = new MessageSendReq().setHeader(new MessageHeader().setNoPersist(1).setRedDot(1));
            messageSendReq.setChanelID(tempChannelId).setChannelType(WK.ChannelType.data);
            String content = uidName + "开播了，快来围观";
            messageSendReq.setFromUID(uid).setPayload(content.getBytes(StandardCharsets.UTF_8));
            wkMessageService.send(messageSendReq);
            // send mq to delay deletion temp channel
            // TODO  :  here need to achieve rocket mq
        }

        // now gift buy channel , stream channel , message delivery channel id can set to redis
        redisCache.setHash(kvBuilder.buildGoLiveKey(uid),kvBuilder.buildGoLiveValue(streamId,giftBuyChannelId,danmakuChannel.getChanelID()));

        // close event
        eventService.commit(eventId);
        return ResKit.ok(map);
    }

    /**
     * 加入直播间
     * @param toUid 主播id
     * @param province 加入省份
     * @return {@link JoinStreamRes}
     */
    @GetMapping("/join{toUid}{province}")
    public SingleResponse join(@PathVariable @NotNull String toUid,@PathVariable @NotNull String province) {
        String uid  = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        if (followApplyService.isBlock(uid,toUid)) {
            return ResKit.fail("你已被" + uidName+ "拉黑");
        }
        Map<Object,Object> liveInfo = redisCache.getHash(kvBuilder.buildGoLiveKey(uid));
        if (liveInfo == null) {
            return ResKit.fail("no live info");
        }
        // check connect status
        Response<List<OnlineStatus>> onlineStatus = wkUserService.onlineStatus(List.of(toUid));
        if (!onlineStatus.isSuccessful()) {
            log.error("wk server error:{}",onlineStatus.errorBody());
            return ResKit.fail("server error");
        }
        if (onlineStatus.body() == null || onlineStatus.body().get(0).getOnline() != 1) {
            log.error("illegal online status uid:{}",uid);
            return ResKit.fail("illegal online status");
        }
        String streamId = (String) liveInfo.get("streamId");
        String giftBuyChannelId = (String) liveInfo.get("giftBuyChannelId");
        String wkChannelId = (String) liveInfo.get("wkChannelId");
        String existsProvince = province;
        if (!provinceService.exist(province)) {
            existsProvince = provinceService.getNearbyProvince(province);
        }
        String edgeServer = srsConfig.getEdgeServerRandom(existsProvince);
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid).eq(Room::getIsAlive, true), false);
        if (room == null) {
            return ResKit.fail("room not exist");
        }
        // check stream info
        StreamRecord stream = streamRecordService.getOne(new LambdaQueryWrapper<StreamRecord>().eq(StreamRecord::getStreamId, streamId));
        if (stream == null || stream.getIsDelete()) {
            return ResKit.fail("stream not exist");
        }
        if (room.getIsBlock()) {
            return ResKit.fail("room is block");
        }
        // add watch num
        redisCache.incrHash(kvBuilder.buildStreamRecordKey(uid,streamId),"watchNum");
        // check whether is fans
        if (followApplyService.isFan(uid,toUid)) {
            redisCache.incrHash(kvBuilder.buildStreamRecordKey(uid,streamId),"watchFanNum");
        }
        // try to add user to wk temp channel
        SubscribersAddReq subscribersAddReq = new SubscribersAddReq().setSubscribers(List.of(uid)).setReset(0).setTempSubscribers(0);
        subscribersAddReq.setChanelID(wkChannelId).setChannelType(WK.ChannelType.data);
        wkChannelService.addSubscribers(subscribersAddReq);
        // send user into room  message
        MessageSendReq sendReq = new MessageSendReq().setHeader(new MessageHeader().setNoPersist(1));
        String content = uidName + "来了";
        sendReq.setFromUID(uid).setPayload(content.getBytes(StandardCharsets.UTF_8));
        sendReq.setChanelID(wkChannelId).setChannelType(WK.ChannelType.data);
        wkMessageService.send(sendReq);
        String specify = stream.getIdentify();
        if (StringKit.isEmpty(specify)) {
            return ResKit.fail("stream url is empty");
        }
        String pullStreamUrl = edgeServer + "/" + specify;
        JoinStreamRes res = new JoinStreamRes().setStreamUrl(pullStreamUrl).setDanmakuId(wkChannelId).setStreamId(streamId).setGiftBuyRecordId(giftBuyChannelId);
        return ResKit.ok(res);
    }

    /**
     * 结束直播
     * @param streamId 直播流
     * @return ok
     */
    @GetMapping("/end{streamId}")
    public SingleResponse end(@PathVariable @NotNull String streamId) {
        String uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();

        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid).eq(Room::getIsAlive, true), false);
        if (room == null) {
            return ResKit.fail("room not exist");
        }
        Map<Object, Object> coll = redisCache.getHash(kvBuilder.buildGoLiveKey(uid));
        if (coll == null) {
            return ResKit.fail("no live info");
        }
        if (StringKit.diff(streamId, coll.get("streamId").toString())) {
            return ResKit.fail("streamId not same");
        }
        String giftBuyChannelId = (String) coll.get("giftBuyChannelId");
        String danmakuId = (String) coll.get("wkChannelId");

        Map<Object, Object> map = redisCache.getHash(kvBuilder.buildStreamRecordKey(uid, streamId));
        if (map == null) {
            return ResKit.fail("stream record not exist");
        }
        Integer watchNum = (Integer) map.get("watchNum");
        Long starNum = (Long) map.get("starNum");
        Integer watchFanNum = (Integer) map.get("watchFanNum");
        Long newFansNum = (Long) map.get("newFansNum");
        // collect gift cost
        long cost = giftBuyRecordService.collectCostMoneyByGiftChannelId(giftBuyChannelId);
        StreamRecord updateRecord = new StreamRecord().setStreamId(streamId).setGiftCost(cost);
        updateRecord.setNewFansNum(newFansNum).setWatchNum(watchFanNum).setStarNum(starNum).setWatchNum(watchNum).setUpdateBy(uid);
        streamRecordService.updateById(updateRecord);
        // send close live info to wk channel
        MessageSendReq sendReq = new MessageSendReq().setHeader(new MessageHeader().setNoPersist(1));
        String content = uidName + "已经离开，稍后再来哦";
        sendReq.setFromUID(uid).setPayload(content.getBytes(StandardCharsets.UTF_8));
        sendReq.setChanelID(danmakuId).setChannelType(WK.ChannelType.data);
        wkMessageService.send(sendReq);
        // TODO  :  send mq to close resource
        return ResKit.ok();
    }
}
