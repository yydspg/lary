package cn.lary.module.gift.component;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.stream.component.LiveCacheComponent;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.entity.GiftCache;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.listener.SynchronizeGiftOrderMessage;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.message.dto.stream.GiftSendNotifyDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.pay.component.AbstractBusinessPayment;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import cn.lary.module.pay.dto.PaymentParamDTO;
import cn.lary.module.pay.listener.gift.GiftOrderActiveDetectionMessage;
import cn.lary.module.pay.plugin.PaymentPluginManager;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.cache.dto.LiveCache;
import cn.lary.module.wallet.dto.TransferDTO;
import cn.lary.module.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftPayment extends AbstractBusinessPayment {


    private final GiftCacheComponent giftCacheComponent;
    private final LiveCacheComponent liveCacheComponent;
    private final GiftOrderService giftOrderService;
    private final PaymentPluginManager paymentPluginManager;
    private final WalletService walletService;
    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO paymentDTO) {
        return BusinessKit.ok();
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO paymentDTO) {
        GiftOrderDTO dto = (GiftOrderDTO) paymentDTO;
        long uid = RequestContext.uid();
        String uidName = RequestContext.name();
        GiftCache gift = giftCacheComponent.getGift(dto.getId());
        long anchorUid = dto.getToUid();
        if (gift == null) {
            return BusinessKit.fail("gift id error");
        }
        LiveCache cache = liveCacheComponent.getLive(dto.getToUid());
        if (cache == null) {
            return BusinessKit.fail("live id error");
        }
        BigDecimal amount = calculate(dto.getNum(),gift.getAmount());
        // no rollback
        GiftOrder order = new GiftOrder()
                .setUid(uid)
                .setAnchorUid(anchorUid)
                .setDanmakuId(cache.getDanmakuId())
                .setStreamId(cache.getStreamId())
                .setGiftNum(dto.getNum())
                .setGiftId(dto.getId())
                .setClient(dto.getClient())
                .setStatus(LARY.PAYMENT.STATUS.INIT);
        giftOrderService.save(order);

        if (verifyPlugin(dto.getPlugin())) {

            PaymentParamDTO param = new PaymentParamDTO()
                    .setAmount(amount)
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
        ResponsePair<PaymentProcessPair> execute = transactionTemplate.execute(status -> {

            ResponsePair<Void> pair = walletService.transfer(new TransferDTO()
                    .setAmount(amount)
                    .setChannel(cache.getStreamId())
                    .setUid(uid)
                    .setToUid(anchorUid)
                    .setCategory(LARY.CHANNEL.TYPE.PERSON));
            if (pair.isFail()) {
                return BusinessKit.fail(pair.getMsg());
            }
            return BusinessKit.ok(new PaymentProcessPair()
                    .setInternal(true));
        });
        if (execute == null ){
            log.error("system error,execute wallet decr error");
            return BusinessKit.fail("system error,execute wallet decr error");
        }
        if (execute.isFail()) {
            return BusinessKit.fail(execute.getMsg());
        }
        messageService.asyncSendRocketMessage(new SynchronizeGiftOrderMessage().setGiftOrder(order));
        messageService.send(new GiftSendNotifyDTO(cache.getDanmakuId(), uid,uidName,dto.getId(),dto.getNum()));
        return execute;
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
        messageService.asyncSendRocketMessage(new GiftOrderActiveDetectionMessage());
    }


    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.GIFT;
    }

    private BigDecimal calculate(long giftNum, BigDecimal giftAmount) {
        return giftAmount.multiply(BigDecimal.valueOf(giftNum));
    }

    private boolean verifyPlugin(Integer plugin) {
        return plugin != null && plugin >= 301 && plugin <= 302;
    }


}
