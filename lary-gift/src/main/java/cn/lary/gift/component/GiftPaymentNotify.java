package cn.lary.gift.component;


import cn.lary.api.message.YutakMessageService;
import cn.lary.common.constant.LARY;
import cn.lary.common.kit.SystemClock;
import cn.lary.gift.entity.AnchorFLow;
import cn.lary.gift.entity.GiftOrder;
import cn.lary.gift.service.AnchorFlowService;
import cn.lary.gift.service.GiftOrderService;
import cn.lary.gift.vo.GiftPaymentNotifyVO;
import cn.lary.payment.component.BusinessPaymentNotify;
import cn.lary.payment.component.PaymentNotifyProcessPair;
import cn.lary.payment.constant.PAYMENT;
import cn.lary.payment.vo.PaymentQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftPaymentNotify extends BusinessPaymentNotify<GiftPaymentNotifyVO> {

    private final AnchorFlowService anchorFlowService;
    private final GiftOrderService giftOrderService;
    private final TransactionTemplate transactionTemplate;
    private final YutakMessageService messageService;
//    private final UserCacheComponent userCacheComponent;


    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.GIFT;
    }


    @Override
    public void whenSuccess(GiftPaymentNotifyVO vo) {
        long oid = vo.getOid();
        GiftOrder order = transactionTemplate.execute(status -> {
            GiftOrder temp = giftOrderService.lambdaQuery()
                    .select(GiftOrder::getOid,GiftOrder::getStatus)
                    .eq(GiftOrder::getOid, oid)
                    .one();
            if (temp == null) {
                log.error("callback gift buy error,oid not exists:{}", oid);
                return null;
            }
            if (temp.getStatus() == PAYMENT.STATUS.FINISH) {
                return temp;
            }
            giftOrderService.lambdaUpdate()
                    .set(GiftOrder::getSn, vo.getTradeNo())
                    .set(GiftOrder::getStatus, PAYMENT.STATUS.FINISH)
                    .set(GiftOrder::getCompleteAt, SystemClock.now())
                    .eq(GiftOrder::getOid, oid)
                    .update();
            anchorFlowService.save(new AnchorFLow(temp));
            return temp;
        });
        if (order == null) {
            return;
        }
//        UserCache pc = userCacheComponent.getData(order.getUid(), LARY.DEVICE.FLAG.PC);
//        UserCache app = userCacheComponent.getData(order.getUid(), LARY.DEVICE.FLAG.APP);
//        if (pc == null && app == null) {
//            log.error("no user join live data when callback order,oid{},uid:{}",oid,order.getUid());
//            return;
//        }
//        String username = "";
//        if (pc != null) {
//            username = pc.getUsername();
//        }
//        if (app != null) {
//            username = app.getUsername();
//        }
//        messageService.send(new GiftSendNotifyDTO(order.getCid(),
//                order.getUid(),
//                username,
//                order.getGid(),
//                order.getNum()));
    }

    @Override
    public void whenFail(GiftPaymentNotifyVO vo) {
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
                    .set(GiftOrder::getStatus, PAYMENT.STATUS.FAIL)
                    .eq(GiftOrder::getOid, oid)
                    .update();
        });
    }

    @Override

    public  GiftPaymentNotifyVO getPaymentNotify(PaymentNotifyProcessPair pair,PaymentQueryVO data) {
        if (pair == null) {
            return new GiftPaymentNotifyVO()
                    .setAmount(data.getAmount())
                    .setOid(data.getPaymentId())
                    .setReason(data.getReason())
                    .setTradeNo(data.getSn());
        }
        return new GiftPaymentNotifyVO(pair);
    }
}
