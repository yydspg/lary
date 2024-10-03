package cn.lary.module.gift.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.StringKit;
import cn.lary.kit.SystemKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.dto.GiftPayParam;
import cn.lary.module.gift.entity.AnchorTurnover;
import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.AnchorTurnoverService;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.service.GiftService;
import cn.lary.module.gift.vo.GiftPayCallbackVO;
import cn.lary.module.pay.core.PayCallback;
import cn.lary.module.pay.core.PayCallbackVO;
import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.stream.dto.JoinLiveCacheDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.user.service.UserService;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftBizExecute  {

    private final AnchorTurnoverService anchorTurnoverService;
    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;
    private final GiftOrderService giftOrderService;
    private final PluginSupport pluginSupport;
    private final UserService userService;
    private final WalletService walletService;
    private final GiftService giftService;

    // external
    private final WKMessageService wkMessageService;

    private final HashMap<Integer,Long> gifts = new HashMap<>();

    /**
     * 下单支付
     * @param uid user id
     * @param req {@link GiftOrderDTO}
     * @return {@link PayBuildVO}
     */
    @Transactional(rollbackFor = Exception.class)
    public ResPair<PayBuildVO> pay(int uid,String uidName, GiftOrderDTO req) {
        // gift id
        Long price = gifts.get(req.getId());
        int anchorUid = req.getToUid();
        if (price == null) {
            return BizKit.fail("gift id error");
        }
        // check pay way and pay client
        Integer client = req.getType();
        Integer payWay = req.getPayWay();
        UserBaseVO anchor = userService.queryBase(anchorUid);
        if (anchor == null) {
            return BizKit.fail("anchor not exists");
        }
        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(anchorUid));
        if (map == null) {
            return BizKit.fail("no live info");
        }
        LiveCacheDTO dto = LiveCacheDTO.of(map);
        // build order
        GiftOrder order = new GiftOrder()
                .setUid(uid)
                .setAnchorUid(anchorUid)
                .setDanmakuId(dto.getWkChannelId())
                .setBuyChannelId(dto.getGiftBuyChannelId())
                .setStreamId(dto.getStreamId())
                .setGiftNum(req.getNum())
                .setGiftId(req.getId())
                .setClientType(req.getType())
                .setStatus(Lary.OrderStatus.init);
        giftOrderService.save(order);
        long cost = gifts.get(req.getId()) * req.getNum();
        // decrease from wallet
        if (payWay == null) {
            Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                    .eq(Wallet::getUid, uid)
                    .eq(Wallet::getIsBlock, false));
            if (wallet == null) {
                return BizKit.fail("wallet not exists,uid:" + uid);
            }
            if (wallet.getVcFee() - wallet.getVcLocked() >= cost ) {
                walletService.update(new LambdaUpdateWrapper<Wallet>()
                        .eq(Wallet::getUid, uid)
                        .set(Wallet::getVcFee, wallet.getVcFee() - cost));
                AnchorTurnover record = new AnchorTurnover()
                        .setAnchorId(req.getToUid())
                        .setBuyUid(uid)
                        .setGiftId(req.getId())
                        .setIncome(cost)
                        .setStreamId(dto.getStreamId())
                        .setGiftNum(req.getNum())
                        .setClientType(req.getType())
                        .setCompleteTime(LocalDateTime.now());
                anchorTurnoverService.save(record);
                String content = uidName + "送出了" + order.getGiftNum() + "个" + order.getGiftName();
                MessageSendDTO sendDTO = new MessageSendDTO()
                        .setHeader(new MessageHeader())
                        .setChannelType(WK.ChannelType.stream)
                        .setChannelID(dto.getWkChannelId())
                        .setPayload(content.getBytes(StandardCharsets.UTF_8));
                wkMessageService.send(sendDTO);
                return BizKit.ok();
            }else {
                return BizKit.fail("钱包余额不足，请充值");
            }
        }
        // build cost

        PayParam payParam = new GiftPayParam().of(order.getOrderId(), cost, client, new HashMap<>());
        PayBuildVO pay = pluginSupport.pay(req.getType(), payWay, payParam);
        return BizKit.ok(pay);
    }

    @PostConstruct
    public void init(){
        List<Gift> list = giftService.list();
        if(CollectionKit.isEmpty(list)) {
            log.error("init gift cache error");
            return ;
        }
        list.forEach(g -> {gifts.put(g.getId(),g.getPrice());});
    }

}
