package cn.lary.module.gift.serviceImpl;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.kit.StringKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.gift.dto.req.GiftOrderReq;
import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.service.GiftService;
import cn.lary.module.pay.core.BizPaymentExecute;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.service.GiftBuyChannelService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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


    public SingleResponse pay(String uid,String loginName,GiftOrderReq req,HttpServletRequest request, HttpServletResponse response) {

        String anchorUid = req.getAnchorUid();
        // check user status
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUid, anchorUid).eq(User::getDeleted,false).eq(User::getStatus, Lary.UserStatus.ok),false);
        if (user == null) {
            return ResKit.fail("user not exist");
        }
        // check anchor status
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, req.getAnchorUid()).eq(Room::getIsBlock, false)
                .eq(Room::getIsDelete, false).eq(Room::getIsAlive, true));
        if (room == null) {
            return ResKit.fail("user is not anchor");
        }
        // check gift id
        String giftId = req.getGiftId();
        Integer GiftNum =  req.getGiftNum();
        Gift gift = giftService.getOne(new LambdaQueryWrapper<Gift>().eq(Gift::getId, giftId).eq(Gift::getIsDelete, false));
        if (gift == null) {
            return ResKit.fail("gift id error");
        }
        // get from redis
        Map<Object, Object> goLiveInfo = redisCache.getHash(kvBuilder.buildGoLiveKey(req.getAnchorUid()));
        if (goLiveInfo == null) {
            return ResKit.fail("go live info not exist");
        }
        String streamId = goLiveInfo.get("streamId").toString();
        String giftBuyChannelId = goLiveInfo.get("giftBuyChannelId").toString();
        String wkChannelId = goLiveInfo.get("wkChannelId").toString();

        if (StringKit.diff(req.getGiftBuyChannelId(),giftBuyChannelId)) {
            return ResKit.fail("no such gift buy channel");
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
