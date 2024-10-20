package cn.lary.module.wallet.component;

import cn.lary.module.app.service.EventService;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.BusinessPaymentNotify;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.entity.RechargeLog;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeLogService;
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
public class WalletPaymentNotifyExecute implements BusinessPaymentNotify {

    private final WalletService walletService;
    private final UserService userService;
    private final RechargeLogService rechargeLogService;
    private final EventService eventService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void onSuccess(PaymentNotifyProcessPair pair) {
        RechargePaymentNotifyVO vo = new RechargePaymentNotifyVO(pair);
        long rechargeId = vo.getRechargeId();
        RechargeLog rechargeLog = transactionTemplate.execute(status -> {

            RechargeLog recharge = rechargeLogService.lambdaQuery()
                    .select(RechargeLog::getId)
                    .select(RechargeLog::getUid)
                    .eq(RechargeLog::getId, rechargeId)
                    .one();
            if (recharge == null) {
                log.error("callback recharge success error, rechargeId no exists:{}", rechargeId);
                return null;
            }
            rechargeLogService.lambdaUpdate()
                    .set(RechargeLog::getSn, vo.getTradeNo())
                    .set(RechargeLog::getCompleteAt, LocalDateTime.now())
                    .set(RechargeLog::getStatus, LARY.PAYMENT.STATUS.FINISH)
                    .eq(RechargeLog::getId, rechargeId);
            Wallet wallet = walletService.lambdaQuery()
                    .select(Wallet::getUid)
                    .eq(Wallet::getUid, recharge.getUid())
                    .one();
            if (wallet == null) {
                log.error("sync wallet error, get wallet error,rechargeId:{},uid:{}", rechargeId, recharge.getUid());
                return null;
            }
            walletService.lambdaUpdate()
                    .setIncrBy(Wallet::getVcFee, recharge.getCost())
                    .eq(Wallet::getUid, recharge.getUid());
            eventService.commit(recharge.getEventId());
            return recharge;
        });
        if (rechargeLog != null) {
            User user = userService.lambdaQuery()
                    .select(User::getPhone)
                    .eq(User::getUid, rechargeLog.getUid())
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
            RechargeLog rechargeLog= rechargeLogService.lambdaQuery()
                    .select(RechargeLog::getId)
                    .eq(RechargeLog::getId, rechargeId)
                    .one();
            if (rechargeLog == null) {
                log.error("callback recharge fail error, rechargeId no exists:{}", rechargeId);
                return;
            }
            rechargeLogService.lambdaUpdate()
                    .set(RechargeLog::getSn,vo.getTradeNo())
                    .set(RechargeLog::getStatus, LARY.PAYMENT.STATUS.FAIL)
                    .set(RechargeLog::getCompleteAt, LocalDateTime.now())
                    .set(RechargeLog::getFailReason,vo.getFailReason())
                    .eq(RechargeLog::getId,vo.getRechargeId());
            eventService.commit(rechargeLog.getEventId());
        });
    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.RECHARGE;
    }
}
