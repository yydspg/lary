package cn.lary.module.gift.core;

import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.vo.GiftPayCallbackVO;
import cn.lary.module.pay.core.PayCallback;
import cn.lary.module.stream.dto.JoinLiveCacheDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftPayCallbackExecute implements PayCallback {

    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;
    private final GiftOrderService giftOrderService;
    // external
    private final WKMessageService wkMessageService;

    @Override
    public void onSuccess(Map<String, String> map,int payWay) {
        GiftPayCallbackVO vo =  new GiftPayCallbackVO().of(map, payWay);
        Long orderId = vo.getGiftOrderId();
        GiftOrder order = giftOrderService.getOne(new LambdaQueryWrapper<GiftOrder>()
                .eq(GiftOrder::getOrderId, orderId));
        if (order == null) {
            log.error("callback gift buy error,orderId not exists:{}",orderId);
            return;
        }
        //update success
        giftOrderService.update(new LambdaUpdateWrapper<GiftOrder>()
                .set(GiftOrder::getSn, vo.getTradeNo())
                .set(GiftOrder::getStatus, Lary.OrderStatus.commit)
                .set(GiftOrder::getCompleteAt, LocalDateTime.now()));
        // process wk channel msg
        // get stream info
        Map<Object, Object> data = redisCache.getHash(kvBuilder.goLiveK(order.getAnchorUid()));
        if (data == null) {
            log.error("no live info when callback order,orderId{},anchorUid:{}",orderId,order.getAnchorUid());
            return ;
        }
        Map<Object, Object> userData = redisCache.getHash(kvBuilder.joinLiveK(order.getUid()));
        if (userData == null) {
            log.error("no user join live data when callback order,orderId{},uid:{}",orderId,order.getUid());
            return;
        }
        JoinLiveCacheDTO dto = JoinLiveCacheDTO.of(userData);
        LiveCacheDTO live = LiveCacheDTO.of(data);
        int danmakuId = live.getWkChannelId();
        String content = dto.getName() + "送出了" + order.getGiftNum() + "个" + order.getGiftName();
        MessageSendDTO sendDTO = new MessageSendDTO()
                .setHeader(new MessageHeader())
                .setChannelType(WK.ChannelType.stream)
                .setChannelID(danmakuId)
                .setPayload(content.getBytes(StandardCharsets.UTF_8));
        wkMessageService.send(sendDTO);
    }

    @Override
    public void onFail(Map<String, String> map,int payWay) {
        GiftPayCallbackVO vo =  new GiftPayCallbackVO().of(map, payWay);
        Long orderId = vo.getGiftOrderId();
        GiftOrder order = giftOrderService.getOne(new LambdaQueryWrapper<GiftOrder>().eq(GiftOrder::getOrderId, orderId));
        if (order == null) {
            log.error("callback on fail error,orderId not exists:{}",orderId);
            return;
        }
        // update fail
        giftOrderService.update(new LambdaUpdateWrapper<GiftOrder>()
                .set(GiftOrder::getSn, vo.getTradeNo())
                .set(GiftOrder::getFailReason,vo.getFailReason())
                .set(GiftOrder::getCompleteAt, LocalDateTime.now())
                .set(GiftOrder::getStatus,Lary.OrderStatus.fail)
                .eq(GiftOrder::getOrderId, orderId));
    }

    @Override
    public Integer getBiz() {
        return Lary.PayBiz.gift;
    }
}
