package cn.lary.module.wallet.core;

import cn.lary.module.app.service.EventService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.pay.core.PayCallback;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.entity.RechargeLog;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeLogService;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.module.wallet.vo.RechargePayCallbackVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class WalletPayCallbackExecute implements PayCallback {

    private final WalletService walletService;
    private final UserService userService;
    private final RechargeLogService rechargeLogService;
    private final EventService eventService;

    @Override
    public void onSuccess(Map<String, String> map, int payWay) {
        RechargePayCallbackVO vo = new RechargePayCallbackVO().of(map, payWay);
        Long rechargeId = vo.getRechargeId();
        RechargeLog recharge = rechargeLogService.getOne(new LambdaQueryWrapper<RechargeLog>()
                .eq(RechargeLog::getRechargeId, rechargeId));
        if (recharge == null) {
            log.error("callback recharge success error, rechargeId no exists:{}", rechargeId);
            return;
        }
        rechargeLogService.update(new LambdaUpdateWrapper<RechargeLog>()
                .set(RechargeLog::getSn,vo.getTradeNo())
                .set(RechargeLog::getCompleteAt, LocalDateTime.now())
                .set(RechargeLog::getStatus, Lary.OrderStatus.commit));
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, recharge.getUid())
                .eq(Wallet::getIsDelete, false),false);
        if (wallet == null) {
            log.error("sync wallet error, get wallet error,rechargeId:{},uid:{}",rechargeId,recharge.getUid());
            return;
        }
        walletService.update(new LambdaUpdateWrapper<Wallet>()
                .set(Wallet::getVcFee,wallet.getVcFee() + recharge.getCost())
                .eq(Wallet::getUid,recharge.getUid()));
        // close event
        eventService.commit(recharge.getEventId());
        // send sms info
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUid, recharge.getUid()));
        SmsFactory.getSmsBlend("test").sendMessageAsync(user.getPhone(),"test");
    }

    @Override
    public void onFail(Map<String, String> map,int payWay) {
        RechargePayCallbackVO vo = new RechargePayCallbackVO().of(map, payWay);
        Long rechargeId = vo.getRechargeId();
        RechargeLog recharge = rechargeLogService.getOne(new LambdaQueryWrapper<RechargeLog>()
                .eq(RechargeLog::getRechargeId, rechargeId));
        if (recharge == null) {
            log.error("callback recharge fail error, rechargeId no exists:{}", rechargeId);
            return;
        }
        rechargeLogService.update(new LambdaUpdateWrapper<RechargeLog>()
                .set(RechargeLog::getSn,vo.getTradeNo())
                .set(RechargeLog::getStatus, Lary.OrderStatus.fail)
                .set(RechargeLog::getCompleteAt, LocalDateTime.now())
                .set(RechargeLog::getFailReason,vo.getFailReason())
                .eq(RechargeLog::getRechargeId,vo.getRechargeId()));
        // close event
        eventService.commit(recharge.getEventId());
    }

    @Override
    public Integer getBiz() {
        return Lary.PayBiz.recharge;
    }
}
