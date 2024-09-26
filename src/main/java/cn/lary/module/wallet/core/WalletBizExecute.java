package cn.lary.module.wallet.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.pay.config.BizPaymentConfig;
import cn.lary.module.pay.core.PayCallback;
import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.kit.PayBizKit;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.pay.service.PaymentLogService;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.dto.RechargeDTO;
import cn.lary.module.wallet.dto.RechargePayParam;
import cn.lary.module.wallet.entity.RechargeLog;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeLogService;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletBizExecute implements PayCallback {

    private final WalletService walletService;
    private final UserService userService;
    private final PluginSupport pluginSupport;
    private final RechargeLogService rechargeLogService;
    private final PaymentLogService paymentLogService;

    /**
     * 充值业务
     * @param req {@link RechargeDTO}
     * @param uid 用户 id
     * @return {@link ResPair} 表示程序内部错误集合
     */
    public ResPair<PayBuildVO> recharge(RechargeDTO req, String uid)  {
        UserBaseVO userbase = userService.queryBase(uid);
        if(userbase == null){
            return BizKit.fail("user not exist");
        }
        // check user wallet status
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>().eq(Wallet::getUid, uid).eq(Wallet::getIsBlock, false).eq(Wallet::getIsDelete, false), false);
        if(wallet == null){
            return BizKit.fail("wallet not exist");
        }
        // build recharge log
        Long cost = req.getSum();
        RechargeLog rechargeLog = new RechargeLog().setClientType(req.getClientType()).setCost(cost).setStarNum(cost).setStatus(Lary.RechargeStatus.noPay);
        rechargeLogService.save(rechargeLog);

        PaymentLog log = new PaymentLog().setOrderType(Lary.OrderType.recharge).setPayCost(cost)
                .setPayWay(req.getPayWay());
        paymentLogService.save(log);
        // build payment log
        int clientType = req.getClientType();
        int payWay = req.getPayWay();
        //  external service
        PayParam param = new RechargePayParam().of(log.getPayId(), cost, clientType, new HashMap<>());
        //execute payment init fail
        PayBuildVO payVO = pluginSupport.pay(clientType, payWay, param);
        if (!payVO.isOk()) {
            rechargeLogService.update(new LambdaUpdateWrapper<RechargeLog>().set(RechargeLog::getStatus,Lary.RechargeStatus.failed).set(RechargeLog::getFailReason,payVO.getErrMsg())
                    .set(RechargeLog::getCompleteAt, LocalDateTime.now()).eq(RechargeLog::getRechargeId, rechargeLog.getRechargeId()));
            return BizKit.fail(payVO.getErrMsg());
        }
        // send info
        // TODO  :  这里可以实现自己返回页面
        return BizKit.ok(payVO);
    }

    public ResPair<Wallet> getBalance(String uid)  {
        // check user status
//        UserBaseVO base = userService.queryBase(uid);
//        if (base == null) {
//            return BizKit.fail("user not exist");
//        }
        // check wallet status
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>().eq(Wallet::getUid, uid).eq(Wallet::getIsDelete,false).eq(Wallet::getIsBlock,false), false);
        if (wallet == null) {
            return  BizKit.fail("wallet status error");
        }
        return BizKit.ok(wallet);
    }

    @Override
    public void onSuccess(Map<String, String> map) {

    }

    @Override
    public void onFail(Map<String, String> map) {

    }

    @Override
    public Integer getBiz() {
        return 0;
    }
}
