package cn.lary.module.wallet.component;

import cn.lary.module.common.service.EventService;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.BusinessPaymentNotify;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.vo.PaymentQueryVO;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.entity.RechargeRecord;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeRecordService;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.module.wallet.vo.RechargePaymentNotifyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RechargePaymentNotify implements BusinessPaymentNotify {


    private final WalletService walletService;
    private final UserService userService;
    private final RechargeRecordService rechargeRecordService;
    private final EventService eventService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void onSuccess(PaymentNotifyProcessPair pair) {

        RechargePaymentNotifyVO vo = new RechargePaymentNotifyVO(pair);
        long rechargeId = vo.getRechargeId();
        RechargeRecord rechargeRecord = transactionTemplate.execute(status -> {

            RechargeRecord recharge = rechargeRecordService.lambdaQuery()
                    .select(RechargeRecord::getId)
                    .select(RechargeRecord::getUid)
                    .eq(RechargeRecord::getId, rechargeId)
                    .one();
            if (recharge == null) {
                log.error("callback recharge success error, rechargeId no exists:{}", rechargeId);
                return null;
            }
            rechargeRecordService.lambdaUpdate()
                    .set(RechargeRecord::getSn, vo.getTradeNo())
                    .set(RechargeRecord::getCompleteAt, LocalDateTime.now())
                    .set(RechargeRecord::getStatus, LARY.PAYMENT.STATUS.FINISH)
                    .eq(RechargeRecord::getId, rechargeId)
                    .update();
            Wallet wallet = walletService.lambdaQuery()
                    .select(Wallet::getUid)
                    .eq(Wallet::getUid, recharge.getUid())
                    .one();
            if (wallet == null) {
                log.error("sync wallet error, get wallet error,rechargeId:{},uid:{}", rechargeId, recharge.getUid());
                return null;
            }
            walletService.lambdaUpdate()
                    .setIncrBy(Wallet::getPocket, recharge.getAmount())
                    .setIncrBy(Wallet::getIncome,recharge.getAmount())
                    .eq(Wallet::getUid, recharge.getUid())
                    .update();
            eventService.commit(recharge.getEventId());
            return recharge;
        });
        if (rechargeRecord != null) {
            User user = userService.lambdaQuery()
                    .select(User::getPhone)
                    .eq(User::getUid, rechargeRecord.getUid())
                    .one();
            SmsFactory.getSmsBlend("test")
                    .sendMessageAsync(user.getPhone(),"test");
        }
    }

    @Override
    public void onFail(PaymentNotifyProcessPair pair) {
        RechargePaymentNotifyVO vo = new RechargePaymentNotifyVO(pair);
        long rechargeId = vo.getRechargeId();
        transactionTemplate.executeWithoutResult(status -> {
            RechargeRecord rechargeRecord = rechargeRecordService.lambdaQuery()
                    .select(RechargeRecord::getId)
                    .eq(RechargeRecord::getId, rechargeId)
                    .one();
            if (rechargeRecord == null) {
                log.error("callback recharge fail error, rechargeId no exists:{}", rechargeId);
                return;
            }
            rechargeRecordService.lambdaUpdate()
                    .set(RechargeRecord::getSn,vo.getTradeNo())
                    .set(RechargeRecord::getStatus, LARY.PAYMENT.STATUS.FAIL)
                    .set(RechargeRecord::getCompleteAt, LocalDateTime.now())
                    .set(RechargeRecord::getFailReason,vo.getFailReason())
                    .eq(RechargeRecord::getId,vo.getRechargeId())
                    .update();
            eventService.commit(rechargeRecord.getEventId());
        });
    }

    @Override
    public void onQuerySuccess(PaymentQueryVO pair) {

    }

    @Override
    public void onQueryFail(PaymentQueryVO pair) {

    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.RECHARGE;
    }
}
