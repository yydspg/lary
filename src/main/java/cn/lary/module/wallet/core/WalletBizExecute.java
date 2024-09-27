package cn.lary.module.wallet.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.StringKit;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.event.dto.RechargeEventDTO;
import cn.lary.module.gift.dto.UpdateSecurityQuestionDTO;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.pay.service.PaymentLogService;
import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.user.service.UserService;
import cn.lary.module.wallet.dto.RechargeDTO;
import cn.lary.module.wallet.dto.RechargePayParam;
import cn.lary.module.wallet.entity.RechargeLog;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeLogService;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.module.wallet.vo.BalanceVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletBizExecute {

    private final WalletService walletService;
    private final PluginSupport pluginSupport;
    private final RechargeLogService rechargeLogService;
    private final PaymentLogService paymentLogService;
    private final EventService eventService;

    /**
     * 充值业务
     * @param req {@link RechargeDTO}
     * @param uid 用户 id
     * @return {@link ResPair} 表示程序内部错误集合
     */
    public ResPair<PayBuildVO> recharge(RechargeDTO req, Integer uid)  {
//        UserBaseVO userbase = userService.queryBase(uid);
//        if(userbase == null){
//            return BizKit.fail("user not exist");
//        }
        // check user wallet status
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>().eq(Wallet::getUid, uid).eq(Wallet::getIsBlock, false).eq(Wallet::getIsDelete, false), false);
        if(wallet == null){
            return BizKit.fail("wallet not exist");
        }

        // build recharge log
        Long cost = req.getSum();
        RechargeLog rechargeLog = new RechargeLog()
                .setClientType(req.getClientType())
                .setCost(cost)
                .setStarNum(cost)
                .setStatus(Lary.OrderStatus.init);
        rechargeLogService.save(rechargeLog);
        // start recharge event
        RechargeEventDTO eventDTO = new RechargeEventDTO(uid, cost, rechargeLog.getRechargeId());
        int eventId = eventService.begin(eventDTO.of());
        rechargeLogService.update(new LambdaUpdateWrapper<RechargeLog>()
                .set(RechargeLog::getEventId, eventId)
                .eq(RechargeLog::getRechargeId,rechargeLog.getRechargeId()));
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
            rechargeLogService.update(new LambdaUpdateWrapper<RechargeLog>()
                    .set(RechargeLog::getStatus,Lary.RechargeStatus.failed)
                    .set(RechargeLog::getFailReason,payVO.getErrMsg())
                    .set(RechargeLog::getCompleteAt, LocalDateTime.now())
                    .eq(RechargeLog::getRechargeId, rechargeLog.getRechargeId()));
            return BizKit.fail(payVO.getErrMsg());
        }

        return BizKit.ok(payVO);
    }

    /**
     * 获取余额
     * @param uid user id
     * @return  {@link BalanceVO}
     */
    public ResPair<BalanceVO> getBalance(Integer uid)  {
        // check user status

        // check wallet status
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, uid)
                .eq(Wallet::getIsDelete,false)
                .eq(Wallet::getIsBlock,false), false);
        if (wallet == null) {
            return  BizKit.fail("wallet status error");
        }
       BalanceVO vo = new BalanceVO()
               .setVcIncome(wallet.getVcIncome())
               .setIsAnchor(wallet.getIsAnchor())
               .setVcFee(wallet.getVcFee())
               .setVcOutcome(wallet.getVcOutcome())
               .setVcLocked(wallet.getVcLocked())
               .setQuestion(wallet.getSecQuestion());
        return BizKit.ok(vo);
    }

    /**
     * 更新问题
     * @param uid user id
     * @param req {@link UpdateSecurityQuestionDTO}
     * @return ok
     */
    public ResPair<Void> updateQuestion(Integer uid, UpdateSecurityQuestionDTO req) {
        // check wallet status
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, uid)
                .eq(Wallet::getIsDelete,false)
                .eq(Wallet::getIsBlock,false), false);
        if (wallet == null) {
            return  BizKit.fail("wallet status error");
        }
        if(StringKit.diff(StringKit.MD5(req.getBeforeAnswer()),wallet.getSecAnswer())) {
            return BizKit.fail("问题回答错误");
        }
        walletService.update(new LambdaUpdateWrapper<Wallet>()
                        .set(Wallet::getSecAnswer,req.getAfterQuestion())
                        .set(Wallet::getSecQuestion,StringKit.MD5(req.getAfterAnswer()))
                        .eq(Wallet::getUid, uid));
        return BizKit.ok();
    }
}
