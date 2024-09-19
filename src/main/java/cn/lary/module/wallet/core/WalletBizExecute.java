package cn.lary.module.wallet.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.pay.config.BizPaymentConfig;
import cn.lary.module.pay.dto.PayBuildDTO;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.pay.service.PaymentLogService;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.dto.RechargeReq;
import cn.lary.module.wallet.entity.RechargeLog;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeLogService;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletBizExecute  {

    private final WalletService walletService;
    private final UserService userService;
    private final PluginSupport pluginSupport;
    private final RechargeLogService rechargeLogService;
    private final BizPaymentConfig bizPaymentConfig;
    private final PaymentLogService paymentLogService;

    /**
     * 充值业务
     * @param req {@link RechargeReq}
     * @param uid 用户 id
     * @return {@link ResPair} 表示程序内部错误集合
     */
    public ResPair<PayBuildDTO> recharge(RechargeReq req, String uid)  {
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
        String rechargeId = UUIDKit.getUUID();
        Integer cost = req.getSum();
        RechargeLog rechargeLog = new RechargeLog().setRechargeId(rechargeId).setClientType(req.getClientType()).setCost(cost).setStarNum(cost).setStatus(Lary.RechargeStatus.noPay);
        rechargeLogService.save(rechargeLog);

        PaymentLog log = new PaymentLog().setOrderId(rechargeId).setOrderType(Lary.OrderType.recharge).setPayCost(new BigDecimal(cost).setScale(2, RoundingMode.HALF_UP))
                .setPayWay(req.getPayWay()).setUid(uid);
        paymentLogService.save(log);
        // build payment log
        Integer clientType = req.getClientType();
        String payWay = getPayWay(req.getPayWay());
        //  external service
        PayParam param = new PayParam().setBiz(Lary.OrderType.recharge).setSn(rechargeId).setClientType(getPayClient(clientType))
                .setSubject(bizPaymentConfig.getRechargePrefix() + uid + "recharge:"+cost).setPrice(cost.doubleValue());
        if (payWay == null) {
            return BizKit.fail("payWay not exist");
        }

        //execute payment init fail
        PayBuildDTO payRes = pluginSupport.pay(clientType, payWay, param);
        if (!payRes.isOk()) {
            rechargeLogService.update(new LambdaUpdateWrapper<RechargeLog>().set(RechargeLog::getStatus,Lary.RechargeStatus.failed).set(RechargeLog::getFailReason,payRes.getErrMsg())
                    .set(RechargeLog::getCompleteAt, LocalDateTime.now()).eq(RechargeLog::getRechargeId, rechargeId));
            return BizKit.fail(payRes.getErrMsg());
        }
        // send info
        // TODO  :  这里可以实现自己返回页面
        return BizKit.ok(payRes);
    }

    public ResPair<Wallet> getBalance(String uid)  {
        // check user status
        UserBaseVO base = userService.queryBase(uid);
        if (base == null) {
            return BizKit.fail("user not exist");
        }
        // check wallet status
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>().eq(Wallet::getUid, uid).eq(Wallet::getIsDelete,false).eq(Wallet::getIsBlock,false), false);
        if (wallet == null) {
            return  BizKit.fail("wallet status error");
        }
        return BizKit.ok(wallet);
    }
    public String getPayClient(Integer clientType) {
        return switch(clientType) {
            case Lary.ClientType.web -> "web";
            case Lary.ClientType.app -> "app";
            default -> null;
        };
    }
    public String getPayWay(Integer payWay) {
        return switch(payWay) {
            case Lary.PayWay.alipay -> "alipay";
            case Lary.PayWay.wechat -> "wechat";
            default -> null;
        };
    }
}
