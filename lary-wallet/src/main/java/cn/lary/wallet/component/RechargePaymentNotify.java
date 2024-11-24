package cn.lary.wallet.component;

import cn.lary.common.constant.LARY;
import cn.lary.common.id.SystemClock;
import cn.lary.payment.component.BusinessPaymentNotify;
import cn.lary.payment.component.PaymentNotifyProcessPair;
import cn.lary.payment.constant.PAYMENT;
import cn.lary.payment.vo.PaymentQueryVO;
import cn.lary.wallet.entity.RechargeRecord;
import cn.lary.wallet.entity.Wallet;
import cn.lary.wallet.service.RechargeRecordService;
import cn.lary.wallet.service.WalletService;
import cn.lary.wallet.vo.RechargePaymentNotifyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RechargePaymentNotify extends BusinessPaymentNotify<RechargePaymentNotifyVO> {


    private final WalletService walletService;
//    private final UserService userService;
    private final RechargeRecordService rechargeRecordService;
//    private final EventService eventService;
    private final TransactionTemplate transactionTemplate;


    @Override
    public void whenSuccess(RechargePaymentNotifyVO vo) {
        long cid = vo.getRechargeId();
        RechargeRecord rechargeRecord = transactionTemplate.execute(status -> {

            RechargeRecord recharge = rechargeRecordService.lambdaQuery()
                    .select(RechargeRecord::getUid,RechargeRecord::getCid)
                    .eq(RechargeRecord::getCid, cid)
                    .one();
            if (recharge == null) {
                log.error("callback recharge success error, cid no exists:{}", cid);
                return null;
            }
            rechargeRecordService.lambdaUpdate()
                    .set(RechargeRecord::getSn, vo.getTradeNo())
                    .set(RechargeRecord::getCompleteAt, SystemClock.now())
                    .set(RechargeRecord::getStatus, PAYMENT.STATUS.FINISH)
                    .eq(RechargeRecord::getId, cid)
                    .update();
            Wallet wallet = walletService.lambdaQuery()
                    .select(Wallet::getUid)
                    .eq(Wallet::getUid, recharge.getUid())
                    .one();
            if (wallet == null) {
                log.error("sync wallet error, get wallet error,cid:{},uid:{}", cid, recharge.getUid());
                return null;
            }
            walletService.lambdaUpdate()
                    .setIncrBy(Wallet::getPocket, recharge.getAmount())
                    .setIncrBy(Wallet::getIncome,recharge.getAmount())
                    .eq(Wallet::getUid, recharge.getUid())
                    .update();
//            eventService.commit(recharge.getEid());
            return recharge;
        });
//        if (rechargeRecord != null) {
//            User user = userService.lambdaQuery()
//                    .select(User::getPhone)
//                    .eq(User::getUid, rechargeRecord.getUid())
//                    .one();
//            SmsFactory.getSmsBlend("test")
//                    .sendMessageAsync(user.getPhone(),"test");
//        }
    }

    @Override
    public void whenFail(RechargePaymentNotifyVO vo) {
        long cid = vo.getRechargeId();
        transactionTemplate.executeWithoutResult(status -> {
            RechargeRecord rechargeRecord = rechargeRecordService.lambdaQuery()
                    .select(RechargeRecord::getId)
                    .eq(RechargeRecord::getId, cid)
                    .one();
            if (rechargeRecord == null) {
                log.error("callback recharge FAIL error, cid no exists:{}", cid);
                return;
            }
            rechargeRecordService.lambdaUpdate()
                    .set(RechargeRecord::getSn,vo.getTradeNo())
                    .set(RechargeRecord::getStatus, PAYMENT.STATUS.FAIL)
                    .set(RechargeRecord::getCompleteAt, LocalDateTime.now())
                    .set(RechargeRecord::getFailReason,vo.getFailReason())
                    .eq(RechargeRecord::getId,vo.getRechargeId())
                    .update();
//            eventService.commit(rechargeRecord.getEid());
        });
    }

    @Override
    public RechargePaymentNotifyVO getPaymentNotify(PaymentNotifyProcessPair pair, PaymentQueryVO data) {
        if (pair == null) {
            return new RechargePaymentNotifyVO()
                    .setRechargeId(data.getPaymentId())
                    .setFailReason(data.getReason())
                    .setAmount(data.getAmount())
                    .setTradeNo(data.getSn());
        }
        return new RechargePaymentNotifyVO(pair);
    }


    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.RECHARGE;
    }
}
