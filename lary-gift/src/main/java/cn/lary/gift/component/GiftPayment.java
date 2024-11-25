package cn.lary.gift.component;

import cn.lary.api.message.YutakMessageService;
import cn.lary.common.constant.LARY;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.rocketmq.YutakMQTemplate;
import cn.lary.gift.entity.GiftOrder;
import cn.lary.gift.service.GiftOrderService;
import cn.lary.payment.component.AbstractBusinessPayment;
import cn.lary.payment.component.PaymentProcessPair;
import cn.lary.payment.constant.PAYMENT;
import cn.lary.payment.dto.BusinessPaymentDTO;
import cn.lary.payment.plugin.PaymentPluginManager;
import cn.lary.payment.vo.PaymentBuildVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftPayment extends AbstractBusinessPayment {


    private final GiftCacheComponent giftCacheComponent;
//    private final LiveCacheComponent liveCacheComponent;
    private final GiftOrderService giftOrderService;
    private final PaymentPluginManager paymentPluginManager;
//    private final WalletService walletService;
    private final YutakMessageService messageService;
//    private final TransactionTemplate transactionTemplate;
    private final YutakMQTemplate yutakMQTemplate;

    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO paymentDTO) {
        return BusinessKit.ok();
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO paymentDTO) {
//        GiftOrderDTO dto = (GiftOrderDTO) paymentDTO;
//        long uid = RequestContext.uid();
//        String uidName = RequestContext.name();
//        GiftCache gift = giftCacheComponent.getGift(dto.getId());
//        long anchorUid = dto.getToUid();
//        if (gift == null) {
//            return BusinessKit.fail("gift id error");
//        }
//        LiveCache cache = liveCacheComponent.getLive(dto.getToUid());
//        if (cache == null) {
//            return BusinessKit.fail("live id error");
//        }
//        BigDecimal amount = calculate(dto.getNum(),gift.getAmount());
//        // no rollback
//        GiftOrder order = new GiftOrder()
//                .setUid(uid)
//                .setAid(anchorUid)
//                .setCid(cache.getCid())
//                .setSid(cache.getSid())
//                .setNum(dto.getNum())
//                .setGid(dto.getId())
//                .setClient(dto.getClient())
//                .setStatus(PAYMENT.STATUS.INIT);
//        giftOrderService.save(order);
//
//        if (verifyPlugin(dto.getPlugin())) {
//
//            PaymentParamDTO param = new PaymentParamDTO()
//                    .setAmount(amount)
//                    .setClientType(dto.getClient())
//                    .setPayId(order.getId())
//                    .setBusinessSign(LARY.BUSINESS.PAYMENT.GIFT)
//                    .setSubject("lary:gift");
//            return BusinessKit.ok(new PaymentProcessPair()
//                    .setParam(param)
//                    .setPaymentWay(dto.getPlugin())
//                    .setPaymentPlugin(dto.getPlugin())
//                    .setInternal(false));
//        }
//        // try
//        ResponsePair<PaymentProcessPair> execute = transactionTemplate.execute(status -> {
//
//            ResponsePair<Void> pair = walletService.transfer(new TransferDTO()
//                    .setAmount(amount)
//                    .setChannel(cache.getSid())
//                    .setUid(uid)
//                    .setToUid(anchorUid)
//                    .setCategory(LARY.CHANNEL.TYPE.PERSON));
//            if (pair.isFail()) {
//                return BusinessKit.fail(pair.getMsg());
//            }
//            return BusinessKit.ok(new PaymentProcessPair()
//                    .setInternal(true));
//        });
//        if (execute == null ){
//            log.error("system error,execute wallet decr error");
//            return BusinessKit.fail("system error,execute wallet decr error");
//        }
//        if (execute.isFail()) {
//            return BusinessKit.fail(execute.getMsg());
//        }
//        messageService.asyncSendRocketMessage(new SynchronizeGiftOrderMessage().setGiftOrder(order));
//        messageService.send(new GiftSendNotifyDTO(cache.getCid(), uid,uidName,dto.getId(),dto.getNum()));
//        return execute;
        return null;
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
                .set(GiftOrder::getStatus, PAYMENT.STATUS.FAIL)
                .set(GiftOrder::getReason, vo.getErrMsg())
                .set(GiftOrder::getCompleteAt, LocalDateTime.now())
                .eq(GiftOrder::getId, vo.getPaymentId())
                .update();
    }

    @Override
    protected void processWhenPaymentSuccess(PaymentBuildVO vo) {
        giftOrderService.lambdaUpdate()
                .set(GiftOrder::getStatus, PAYMENT.STATUS.COMMIT)
                .eq(GiftOrder::getId, vo.getPaymentId())
                .update();
//        yutakMQTemplate.asyncSendRocketMessage(new GiftOrderActiveDetectionMessage());
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
