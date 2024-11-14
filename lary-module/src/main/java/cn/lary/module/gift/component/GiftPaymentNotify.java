package cn.lary.module.gift.component;

import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.CacheComponent;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.gift.entity.AnchorFLow;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.AnchorFlowService;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.vo.GiftPaymentNotifyVO;
import cn.lary.module.message.dto.stream.GiftSendNotifyDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.pay.component.BusinessPaymentNotify;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.vo.PaymentQueryVO;
import cn.lary.module.cache.dto.JoinLiveCacheDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftPaymentNotify implements BusinessPaymentNotify {

    private final AnchorFlowService anchorFlowService;
    private final CacheComponent cacheComponent;
    private final KVBuilder kvBuilder;
    private final GiftOrderService giftOrderService;
    private final TransactionTemplate transactionTemplate;
    private final MessageService messageService;


    @Override
    public void onSuccess(PaymentNotifyProcessPair pair) {
        processSuccess(pair,null);
    }

    @Override
    public void onFail(PaymentNotifyProcessPair pair) {
        processFail(pair,null);
    }

    @Override
    public void onQuerySuccess(PaymentQueryVO pair) {
        processSuccess(null,pair);
    }

    @Override
    public void onQueryFail(PaymentQueryVO pair) {
        processFail(null,pair);
    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.GIFT;
    }


    private void processSuccess(PaymentNotifyProcessPair pair,PaymentQueryVO queryData) {
        GiftPaymentNotifyVO vo = getPaymentNotify(pair, queryData);
        Long orderId = vo.getGiftOrderId();
        GiftOrder order = transactionTemplate.execute(status -> {
            GiftOrder temp = giftOrderService.lambdaQuery()
                    .select(GiftOrder::getId)
                    .eq(GiftOrder::getId, orderId)
                    .one();
            if (temp == null) {
                log.error("callback gift buy error,orderId not exists:{}", orderId);
                return null;
            }
            giftOrderService.lambdaUpdate()
                    .set(GiftOrder::getSn, vo.getTradeNo())
                    .set(GiftOrder::getStatus, LARY.PAYMENT.STATUS.FINISH)
                    .set(GiftOrder::getCompleteAt, LocalDateTime.now())
                    .eq(GiftOrder::getId, orderId);
            anchorFlowService.save(new AnchorFLow(temp));
            return temp;
        });
        if (order == null) {
            return;
        }
        Map<Object, Object> data = cacheComponent.getHash(kvBuilder.joinLiveK(order.getUid()));
        if (data == null) {
            log.error("no user join live data when callback order,orderId{},uid:{}",orderId,order.getUid());
            return;
        }
        JoinLiveCacheDTO dto = JoinLiveCacheDTO.of(data);
        messageService.send(new GiftSendNotifyDTO(order.getDanmakuId(),
                order.getUid(),
                dto.getName(),
                order.getGiftId(),
                order.getGiftNum()));
    }

    private void processFail(PaymentNotifyProcessPair pair,PaymentQueryVO data) {
        GiftPaymentNotifyVO vo = getPaymentNotify(pair, data);
        long orderId = vo.getGiftOrderId();
        transactionTemplate.executeWithoutResult(status -> {
            GiftOrder order = giftOrderService.lambdaQuery()
                    .select(GiftOrder::getId)
                    .eq(GiftOrder::getId, orderId)
                    .one();
            if (order == null) {
                log.error("callback on FAIL error,order not exists,order id:{}",orderId);
                return;
            }
            giftOrderService.lambdaUpdate()
                    .set(GiftOrder::getSn, vo.getTradeNo())
                    .set(GiftOrder::getFailReason,vo.getFailReason())
                    .set(GiftOrder::getCompleteAt, LocalDateTime.now())
                    .set(GiftOrder::getStatus, LARY.PAYMENT.STATUS.FAIL)
                    .eq(GiftOrder::getId, orderId);
        });
    }

    private GiftPaymentNotifyVO getPaymentNotify(PaymentNotifyProcessPair pair,PaymentQueryVO data) {
        GiftPaymentNotifyVO vo = null;
        if (pair == null) {
            vo = new GiftPaymentNotifyVO()
                    .setCost(data.getAmount())
                    .setGiftOrderId(data.getPaymentId())
                    .setFailReason(data.getReason())
                    .setTradeNo(data.getSn());
        }else {
            vo = new GiftPaymentNotifyVO(pair);
        }
        return vo;
    }
}
