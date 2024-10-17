package cn.lary.module.wallet.core;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.StringKit;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.event.dto.RechargeEventDTO;
import cn.lary.module.wallet.dto.*;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.pay.service.PaymentLogService;
import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.wallet.entity.RechargeLog;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeLogService;
import cn.lary.module.wallet.service.WalletIncomeService;
import cn.lary.module.wallet.service.WalletOutcomeService;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.module.wallet.vo.BalanceVO;
import cn.lary.module.wallet.vo.WalletIncomeVO;
import cn.lary.module.wallet.vo.WalletOutcomeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletBizExecute {

    private final WalletService walletService;
    private final PluginSupport pluginSupport;
    private final RechargeLogService rechargeLogService;
    private final PaymentLogService paymentLogService;
    private final WalletIncomeService walletIncomeService;
    private final WalletOutcomeService walletOutcomeService;
    private final EventService eventService;

    /**
     * 充值业务
     * @param req {@link RechargeDTO}
     * @return {@link PayBuildVO}
     */
    public ResponsePair<PayBuildVO> recharge(RechargeDTO req)  {
        int uid = RequestContext.getLoginUID();
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, uid)
                .eq(Wallet::getIsBlock, false)
                .eq(Wallet::getIsDelete, false), false);
        if(wallet == null){
            return BizKit.fail("wallet not exist");
        }

        Long cost = req.getSum();
        RechargeLog rechargeLog = new RechargeLog()
                .setClientType(req.getClientType())
                .setCost(cost)
                .setStarNum(cost)
                .setStatus(Lary.OrderStatus.init);
        rechargeLogService.save(rechargeLog);
        RechargeEventDTO eventDTO = new RechargeEventDTO(uid, cost, rechargeLog.getRechargeId());
        int eventId = eventService.begin(eventDTO.of());
        rechargeLogService.update(new LambdaUpdateWrapper<RechargeLog>()
                .set(RechargeLog::getEventId, eventId)
                .eq(RechargeLog::getRechargeId,rechargeLog.getRechargeId()));
        PaymentLog log = new PaymentLog()
                .setOrderType(Lary.OrderType.recharge)
                .setPayCost(cost)
                .setPayWay(req.getPayWay());
        paymentLogService.save(log);
        int clientType = req.getClientType();
        int payWay = req.getPayWay();
        PayParam param = new RechargePayParam()
                .of(log.getPayId(), cost, clientType, new HashMap<>());
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
     * @return  {@link BalanceVO}
     */
    public ResponsePair<BalanceVO> getBalance()  {
        int uid = RequestContext.getLoginUID();
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
     * @param req {@link UpdateSecurityQuestionDTO}
     * @return ok
     */
    public ResponsePair<Void> updateQuestion(UpdateSecurityQuestionDTO req) {
        int uid = RequestContext.getLoginUID();
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, uid)
                .eq(Wallet::getIsBlock,false), false);
        if (wallet == null) {
            return  BizKit.fail("wallet status error");
        }
        if(StringKit.diff(StringKit.MD5(req.getBeforeAnswer()),wallet.getSecAnswer())) {
            return BizKit.fail("question answer error");
        }
        walletService.update(new LambdaUpdateWrapper<Wallet>()
                        .set(Wallet::getSecAnswer,req.getAfterQuestion())
                        .set(Wallet::getSecQuestion,StringKit.MD5(req.getAfterAnswer()))
                        .eq(Wallet::getUid, uid));
        return BizKit.ok();
    }

    /**
     * 分页查询收入
     * @param dto {@link WalletIncomePageQueryDTO}
     * @return {@link WalletIncomeVO}
     */
    public ResponsePair<List<WalletIncomeVO>> getIncomeVOs(WalletIncomePageQueryDTO dto){
        int uid = RequestContext.getLoginUID();
        dto.setUid(uid);
        List<WalletIncomeVO> vos = walletIncomeService.getWalletIncomeVOs(dto);
        if(CollectionKit.isEmpty(vos)){
            return  BizKit.fail("wallet income empty");
        }
        return BizKit.ok(vos);
    }

    /**
     * 分页查询支出
     * @param dto {@link WalletOutcomePageQueryDTO}
     * @return {@link WalletOutcomeVO}
     */
    public ResponsePair<List<WalletOutcomeVO>> getOutcomeVOs(WalletOutcomePageQueryDTO dto){
        int uid = RequestContext.getLoginUID();
        dto.setUid(uid);
        List<WalletOutcomeVO> vos = walletOutcomeService.getWalletOutcomeVOs(dto);
        if(CollectionKit.isEmpty(vos)) {
            return  BizKit.fail("wallet outcome empty");
        }
        return BizKit.ok(vos);
    }
}
