package cn.lary.module.wallet.serviceImpl;

import cn.lary.LaryApplication;
import cn.lary.core.dto.ResPair;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.pay.config.BizPaymentConfig;
import cn.lary.module.pay.core.PaymentClientEnum;
import cn.lary.module.pay.dto.PayBuildRes;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.pay.service.PaymentLogService;
import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.dto.req.RechargeReq;
import cn.lary.module.wallet.entity.RechargeLog;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeLogService;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
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
    private final LaryApplication laryApplication;
    private final RechargeLogService rechargeLogService;
    private final BizPaymentConfig bizPaymentConfig;
    private final PaymentLogService paymentLogService;

    /**
     * 充值业务
     * @param response {@link HttpServletResponse} 需要直接对response 写出form表单
     * @param req {@link RechargeReq}
     * @param uid 用户 id
     * @return {@link ResPair} 表示程序内部错误集合
     * @throws IOException
     */
    public ResPair recharge( HttpServletResponse response, RechargeReq req, String uid) throws IOException {
        // check user status
        UserBaseRes userbase = userService.queryBase(uid);
        if(userbase == null){
            return new ResPair().setOk(false).setMsg("user not exist");
        }
        // check user wallet status
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>().eq(Wallet::getUid, uid).eq(Wallet::getIsBlock, false).eq(Wallet::getIsDelete, false), false);
        if(wallet == null){
            return new ResPair().setOk(false).setMsg("wallet not exist");
        }
        // build recharge log
        String rechargeId = UUIDKit.getUUID();
        Integer cost = req.getSum();
        RechargeLog rechargeLog = new RechargeLog().setRechargeId(rechargeId).setClientType(req.getClientType()).setCost(cost).setStarNum(cost).setStatus(Lary.RechargeStatus.noPay);
        rechargeLogService.save(rechargeLog);

        PaymentLog log = new PaymentLog().setOrderId(rechargeId).setOrderType(Lary.OrderType.recharge).setPayCost(new BigDecimal(cost).setScale(2, RoundingMode.HALF_UP))
                .setPayWay(req.getPayWay()).setUid(uid);
        paymentLogService.save(log);
        //  external service
        PayParam param = new PayParam().setBiz(Lary.OrderType.recharge).setSn(rechargeId).setClientType(PaymentClientEnum.getByCode(req.getClientType()).getDesc())
                .setSubject(bizPaymentConfig.getRechargePrefix() + uid + "recharge:"+cost).setPrice(cost.doubleValue());
        // build payment log
        Integer clientType = req.getClientType();
        String payWay = getPayWay(req.getPayWay());
        if (payWay == null) {
            return new ResPair().setOk(false).setMsg("payWay not exist");
        }

        //execute payment init fail
        PayBuildRes payRes = pluginSupport.pay(clientType, payWay, param, response);
        if (!payRes.isOk()) {
            rechargeLogService.update(new LambdaUpdateWrapper<RechargeLog>().set(RechargeLog::getStatus,Lary.RechargeStatus.failed).set(RechargeLog::getFailReason,payRes.getErrMsg())
                    .set(RechargeLog::getCompleteAt, LocalDateTime.now()).eq(RechargeLog::getRechargeId, rechargeId));
            return new ResPair().setOk(false).setMsg(payRes.getErrCode());
        }
        // send info
        // TODO  :  这里可以实现自己返回页面
        response.setContentType(payRes.getContentType());
        PrintWriter out = response.getWriter();
        out.write(payRes.getBody());
        out.flush();
        out.close();
        return new ResPair().setOk(true);
    }

    public String getPayWay(Integer clientType) {
        return switch(clientType) {
            case Lary.ClientType.web -> "web";
            case Lary.ClientType.app -> "app";
            default -> null;
        };
    }

}
