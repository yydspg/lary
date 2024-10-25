package cn.lary.module.wallet.component;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.dto.RechargeEventDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.pay.component.AbstractBusinessPayment;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import cn.lary.module.pay.dto.PaymentParamDTO;
import cn.lary.module.pay.listener.recharge.RechargeActiveDetectionMessage;
import cn.lary.module.pay.plugin.PaymentPluginManager;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.wallet.dto.RechargeDTO;
import cn.lary.module.wallet.entity.RechargeLog;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeLogService;
import cn.lary.module.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletPayment extends AbstractBusinessPayment{

    private final PaymentPluginManager paymentPluginManager;
    private final WalletService walletService;
    private final RechargeLogService rechargeLogService;
    private final EventService eventService;
    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO dto) {
        return null;
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO rechargeDTO) {
        RechargeDTO dto = (RechargeDTO) rechargeDTO;
        int uid = RequestContext.getLoginUID();
        RechargeLog data = transactionTemplate.execute(status -> {
            Wallet wallet = walletService.lambdaQuery()
                    .select(Wallet::getUid)
                    .eq(Wallet::getUid, uid)
                    .one();
            if (wallet == null) {
                return null;
            }
            Long cost = dto.getAmount();
            RechargeLog rechargeLog = new RechargeLog()
                    .setClientType(dto.getClient())
                    .setCost(cost)
                    .setStarNum(cost)
                    .setStatus(LARY.PAYMENT.STATUS.INIT);
            rechargeLogService.save(rechargeLog);
            RechargeEventDTO eventDTO = new RechargeEventDTO(uid, cost, rechargeLog.getId());
            int eventId = eventService.begin(eventDTO);
            rechargeLogService.lambdaUpdate()
                    .set(RechargeLog::getEventId, eventId)
                    .eq(RechargeLog::getId, rechargeLog.getId());
            return rechargeLog;
        });
        if (data == null) {
            return BusinessKit.fail("wallet error");
        }
        PaymentParamDTO param = new PaymentParamDTO()
                .setAmount(dto.getAmount())
                .setClientType(dto.getClient())
                .setPayId(data.getId())
                .setBusinessSign(LARY.BUSINESS.PAYMENT.RECHARGE)
                .setSubject("lary:recharge");
        return BusinessKit.ok(new PaymentProcessPair()
                .setParam(param)
                .setPaymentWay(dto.getClient())
                .setPaymentPlugin(dto.getPlugin()));
    }

    @Override
    protected void afterPay(BusinessPaymentDTO dto) {
        RechargeDTO rechargeDTO = (RechargeDTO) dto;
        log.info("process recharge payment,amount:{},client:{},payWay:{} "
                    ,rechargeDTO.getAmount(),rechargeDTO.getClient(),rechargeDTO.getPlugin());
    }

    @Override
    protected PaymentBuildVO doPay(PaymentProcessPair pair) {
        return paymentPluginManager.pay(pair);
    }


    @Override
    protected void processWhenPaymentFail(PaymentBuildVO vo) {
        rechargeLogService.lambdaUpdate()
                .set(RechargeLog::getStatus, LARY.PAYMENT.STATUS.FAIL)
                .set(RechargeLog::getFailReason,vo.getErrMsg())
                .set(RechargeLog::getCompleteAt, LocalDateTime.now())
                .eq(RechargeLog::getId, vo.getPaymentId());
    }

    @Override
    protected void processWhenPaymentSuccess(PaymentBuildVO vo) {
        rechargeLogService.lambdaUpdate()
                .set(RechargeLog::getStatus, LARY.PAYMENT.STATUS.COMMIT)
                .eq(RechargeLog::getId, vo.getPaymentId());
        messageService.asyncSendRocketMessage(new RechargeActiveDetectionMessage());
    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.RECHARGE;
    }
}
