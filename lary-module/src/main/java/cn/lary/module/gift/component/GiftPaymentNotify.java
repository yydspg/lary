package cn.lary.module.gift.component;


import cn.lary.module.common.constant.LARY;
import cn.lary.module.gift.entity.AnchorFLow;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.AnchorFlowService;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.vo.GiftPaymentNotifyVO;
import cn.lary.module.id.SystemClock;
import cn.lary.module.message.dto.stream.GiftSendNotifyDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.pay.component.BusinessPaymentNotify;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.vo.PaymentQueryVO;
import cn.lary.module.user.component.UserCache;
import cn.lary.module.user.component.UserCacheComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftPaymentNotify implements BusinessPaymentNotify {

    private final AnchorFlowService anchorFlowService;
    private final GiftOrderService giftOrderService;
    private final TransactionTemplate transactionTemplate;
    private final MessageService messageService;
    private final UserCacheComponent userCacheComponent;

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
        long oid = vo.getOid();
        GiftOrder order = transactionTemplate.execute(status -> {
            GiftOrder temp = giftOrderService.lambdaQuery()
                    .select(GiftOrder::getId)
                    .eq(GiftOrder::getId, oid)
                    .one();
            if (temp == null) {
                log.error("callback gift buy error,oid not exists:{}", oid);
                return null;
            }
            giftOrderService.lambdaUpdate()
                    .set(GiftOrder::getSn, vo.getTradeNo())
                    .set(GiftOrder::getStatus, LARY.PAYMENT.STATUS.FINISH)
                    .set(GiftOrder::getCompleteAt, LocalDateTime.now())
                    .eq(GiftOrder::getId, oid)
                    .update();
            anchorFlowService.save(new AnchorFLow(temp));
            return temp;
        });
        if (order == null) {
            return;
        }
        UserCache pc = userCacheComponent.getData(order.getUid(), LARY.DEVICE.FLAG.PC);
        UserCache app = userCacheComponent.getData(order.getUid(), LARY.DEVICE.FLAG.APP);
        if (pc == null && app == null) {
            log.error("no user join live data when callback order,oid{},uid:{}",oid,order.getUid());
            return;
        }
        String username = "";
        if (pc != null) {
           username = pc.getUsername();
        }
        if (app != null) {
            username = app.getUsername();
        }
        messageService.send(new GiftSendNotifyDTO(order.getCid(),
                order.getUid(),
                username,
                order.getGid(),
                order.getNum()));
    }

    private void processFail(PaymentNotifyProcessPair pair,PaymentQueryVO data) {
        GiftPaymentNotifyVO vo = getPaymentNotify(pair, data);
        long oid = vo.getOid();
        transactionTemplate.executeWithoutResult(status -> {
            GiftOrder order = giftOrderService.lambdaQuery()
                    .select(GiftOrder::getOid)
                    .eq(GiftOrder::getOid, oid)
                    .one();
            if (order == null) {
                log.error("callback on FAIL error,order not exists,order id:{}",oid);
                return;
            }
            giftOrderService.lambdaUpdate()
                    .set(GiftOrder::getSn, vo.getTradeNo())
                    .set(GiftOrder::getReason,vo.getReason())
                    .set(GiftOrder::getCompleteAt, SystemClock.now())
                    .set(GiftOrder::getStatus, LARY.PAYMENT.STATUS.FAIL)
                    .eq(GiftOrder::getOid, oid)
                    .update();
        });
    }

    private GiftPaymentNotifyVO getPaymentNotify(PaymentNotifyProcessPair pair,PaymentQueryVO data) {
        if (pair == null) {
            return new GiftPaymentNotifyVO()
                    .setCost(data.getAmount())
                    .setOid(data.getPaymentId())
                    .setReason(data.getReason())
                    .setTradeNo(data.getSn());
        }
        return new GiftPaymentNotifyVO(pair);
    }
}
