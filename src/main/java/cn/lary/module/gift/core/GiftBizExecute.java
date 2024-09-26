package cn.lary.module.gift.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.dto.GiftPayParam;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.pay.core.PayCallback;
import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.user.service.UserService;
import cn.lary.module.user.vo.UserBaseVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftBizExecute implements PayCallback {

    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;
    private final GiftOrderService giftOrderService;
    private final PluginSupport pluginSupport;
    private final UserService userService;

    private final HashMap<String,Long> gifts = new HashMap<>();

    /**
     * 下单支付
     * @param uid user id
     * @param req {@link GiftOrderDTO}
     * @return {@link PayBuildVO}
     */
    public ResPair<PayBuildVO> pay(String uid, GiftOrderDTO req) {
        // gift id
        Long price = gifts.get(req.getId());
        String anchorUid = req.getToUid();
        if (price == null) {
            return BizKit.fail("gift id error");
        }
        // check pay way and pay client
        Integer client = req.getType();
        Integer payWay = req.getPayWay();
        // check anchor status
        UserBaseVO anchor = userService.queryBase(anchorUid);
        if (anchor == null) {
            return BizKit.fail("anchor not exists");
        }
        // check live info
        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(anchorUid));
        if (map == null) {
            return BizKit.fail("no live info");
        }
        LiveCacheDTO dto = LiveCacheDTO.of(map);
        // build order
        GiftOrder order = new GiftOrder()
                .setGiftId(req.getId())
                .setUid(uid)
                .setAnchorUid(anchorUid)
                .setDanmakuId(dto.getWkChannelId())
                .setBuyChannelId(dto.getGiftBuyChannelId())
                .setStreamId(dto.getStreamId())
                .setGiftNum(req.getNum())
                .setClientType(req.getType())
                .setStatus(Lary.OrderStatus.init);
        giftOrderService.save(order);
        // build cost
        Long cost = gifts.get(req.getId()) * req.getNum();
        PayParam payParam = new GiftPayParam().of(order.getOrderId(), cost, client, new HashMap<>());
        PayBuildVO pay = pluginSupport.pay(req.getType(), payWay, payParam);
        return BizKit.ok(pay);
    }
    @PostConstruct
    public void init(){
        log.info("init gift biz execute");
    }

    @Override
    public void onSuccess(Map<String, String> map) {

    }

    @Override
    public void onFail(Map<String, String> map) {

    }

    @Override
    public Integer getBiz() {
        return Lary.PayBiz.gift;
    }
}
