package cn.lary.module.gift.execute;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.entity.AnchorTurnover;
import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.AnchorTurnoverService;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.message.dto.stream.GiftSendNotifyDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.pay.component.AbstractBusinessPayment;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import cn.lary.module.pay.dto.PaymentParamDTO;
import cn.lary.module.pay.plugin.PaymentPluginManager;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.entity.WalletOutcome;
import cn.lary.module.wallet.service.WalletOutcomeService;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.pkg.wk.constant.WK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftPayment extends AbstractBusinessPayment {

    private final AnchorTurnoverService anchorTurnoverService;
    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;
    private final GiftOrderService giftOrderService;
    private final PaymentPluginManager paymentPluginManager;
    private final WalletService walletService;
    private final WalletOutcomeService walletOutcomeService;
    private final HashMap<Integer, Gift> gifts = new HashMap<>();
    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO dto) {
        return null;
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO paymentDTO) {
        GiftOrderDTO dto = (GiftOrderDTO) paymentDTO;
        int uid = RequestContext.getLoginUID();
        String uidName = RequestContext.getLoginName();
        Gift gift = gifts.get(dto.getId());
        int anchorUid = dto.getToUid();
        if (gift == null) {
            return BusinessKit.fail("gift id error");
        }
        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(anchorUid));
        if (map == null) {
            return BusinessKit.fail("no live info");
        }
        LiveCacheDTO cache = LiveCacheDTO.of(map);
        long cost = calculate(dto.getNum(),gift.getPrice());
        // TODO  :  这里加强下这个 判断是否直接充值的逻辑
        if (isValidPlugin(dto.getPlugin())) {
            GiftOrder order = new GiftOrder()
                    .setUid(uid)
                    .setAnchorUid(anchorUid)
                    .setDanmakuId(cache.getDanmakuId())
                    .setStreamId(cache.getStreamId())
                    .setGiftNum(dto.getNum())
                    .setGiftId(dto.getId())
                    .setClientType(dto.getClient())
                    .setStatus(LARY.PAYMENT.STATUS.INIT);
            giftOrderService.save(order);
            PaymentParamDTO param = new PaymentParamDTO()
                    .setAmount(cost)
                    .setClientType(dto.getClient())
                    .setPayId(order.getId())
                    .setBusinessSign(LARY.BUSINESS.PAYMENT.GIFT)
                    .setSubject("lary:gift");
            return BusinessKit.ok(new PaymentProcessPair()
                    .setParam(param)
                    .setPaymentWay(dto.getPlugin())
                    .setPaymentPlugin(dto.getPlugin())
                    .setInternal(false));
        }
        // try
        return transactionTemplate.execute(status -> {
            Wallet wallet = walletService.lambdaQuery()
                    .select(Wallet::getVcLocked,Wallet::getVcFee)
                    .eq(Wallet::getUid, uid)
                    .one();
            if (wallet == null) {
                return BusinessKit.fail("wallet not exists,uid:" + uid);
            }
            if (wallet.getVcFee() - wallet.getVcLocked() >= cost ) {
                walletOutcomeService.save(new WalletOutcome()
                        .setUid(uid)
                        .setToUid(anchorUid)
                        .setChannelId(cache.getStreamId())
                        .setChannelType(WK.ChannelType.stream)
                        .setType(LARY.BUSINESS.TRANSFER.STREAM_GIFT)
                        .setCost(cost));
                walletService.lambdaUpdate()
                        .eq(Wallet::getUid, uid)
                        .setDecrBy(Wallet::getVcFee,cost);
                AnchorTurnover record = new AnchorTurnover()
                        .setAnchorId(dto.getToUid())
                        .setBuyUid(uid)
                        .setGiftId(dto.getId())
                        .setIncome(cost)
                        .setStreamId(cache.getStreamId())
                        .setGiftNum(dto.getNum())
                        .setClientType(dto.getClient())
                        .setCompleteTime(LocalDateTime.now());
                anchorTurnoverService.save(record);
                messageService.send(new GiftSendNotifyDTO(cache.getDanmakuId(),
                        uid,uidName,"test",dto.getNum()));
            }else {
                return BusinessKit.fail("余额不足请充值");
            }
            return BusinessKit.ok(new PaymentProcessPair()
                    .setInternal(true));
        });
    }

    @Override
    protected void afterPay(BusinessPaymentDTO dto) {

    }

    @Override
    protected PaymentBuildVO doPay(PaymentProcessPair pair) {
        return paymentPluginManager.pay(pair);
    }

    @Override
    protected void processWhenPaymentFail(PaymentBuildVO vo) {
        giftOrderService.lambdaUpdate()
                .set(GiftOrder::getStatus, LARY.PAYMENT.STATUS.FAIL)
                .set(GiftOrder::getFailReason, vo.getErrMsg())
                .set(GiftOrder::getCompleteAt, LocalDateTime.now())
                .eq(GiftOrder::getId, vo.getPaymentId());
    }

    @Override
    protected void processWhenPaymentSuccess(PaymentBuildVO vo) {
        giftOrderService.lambdaUpdate()
                .set(GiftOrder::getStatus, LARY.PAYMENT.STATUS.COMMIT)
                .eq(GiftOrder::getId, vo.getPaymentId());
    }


    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.GIFT;
    }

    private long calculate(long giftNum,long giftCost) {
        return 0;
    }
    private boolean isValidPlugin(Integer plugin) {
        return plugin != null && plugin >= 301 && plugin <= 302;
    }
}
