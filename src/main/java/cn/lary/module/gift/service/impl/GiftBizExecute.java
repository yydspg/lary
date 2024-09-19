package cn.lary.module.gift.service.impl;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.StringKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.service.GiftService;
import cn.lary.module.pay.dto.PayBuildDTO;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.service.GiftBuyChannelService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftBizExecute  {

    private final PluginSupport pluginSupport;
    private final GiftOrderService giftOrderService;
    private final UserService userService;
    private final GiftBuyChannelService giftBuyChannelService;
    private final RedisCache redisCache;
    private final StreamRecordService streamRecordService;
    private final RoomService roomService;
    private final WalletService walletService;
    private final KVBuilder kvBuilder;
    private final GiftService giftService;


    public ResPair<PayBuildDTO> pay(String uid, String loginName, GiftOrderDTO req) {

        String anchorUid = req.getAnchorUid();
        // check user status
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUid, anchorUid).eq(User::getDeleted,false).eq(User::getStatus, Lary.UserStatus.ok),false);
        if (user == null) {
            return BizKit.fail("user not exist");
        }
        // check anchor status
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, req.getAnchorUid()).eq(Room::getIsBlock, false)
                .eq(Room::getIsDelete, false).eq(Room::getIsAlive, true));
        if (room == null) {
            return BizKit.fail("room not exist");
        }
        // check gift id
        String giftId = req.getGiftId();
        Integer GiftNum =  req.getGiftNum();
        Gift gift = giftService.getOne(new LambdaQueryWrapper<Gift>().eq(Gift::getId, giftId).eq(Gift::getIsDelete, false));
        if (gift == null) {
            return BizKit.fail("gift not exist");
        }
        // get from redis
        Map<Object, Object> goLiveInfo = redisCache.getHash(kvBuilder.goLiveK(req.getAnchorUid()));
        if (goLiveInfo == null) {
            return BizKit.fail("go live not exist");
        }
        String streamId = goLiveInfo.get("streamId").toString();
        String giftBuyChannelId = goLiveInfo.get("giftBuyChannelId").toString();
        String wkChannelId = goLiveInfo.get("wkChannelId").toString();

        if (StringKit.diff(req.getGiftBuyChannelId(),giftBuyChannelId)) {
            return BizKit.fail("gift buyChannelId not match");
        }
        // check wallet

        // build gift order
        GiftOrder giftOrder = new GiftOrder().setGiftId(giftId).setGiftNum(req.getGiftNum()).setAnchorUid(anchorUid).setDanmakuId(wkChannelId)
                .setCreateBy(uid).setIsSync(false).setIsDelete(false).setUid(uid).setClientType(req.getClientType()).setStreamId(streamId);



        // query gift buy channel
//        String giftBuyChannelId = req.getGiftBuyChannelId();
//        GiftBuyChannel giftBuyChannel = giftBuyChannelService.getOne(new LambdaQueryWrapper<GiftBuyChannel>().eq(GiftBuyChannel::getChannelId, giftBuyChannelId).eq(GiftBuyChannel::getIsAlive, true), false);
//        if (giftBuyChannel == null) {
//            return ResKit.fail("giftBuyChannel not exist or not alive");
//        }

        return null;
    }


}
