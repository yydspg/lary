package cn.lary.module.wallet.component;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.common.service.EventService;
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
import cn.lary.module.wallet.entity.RechargeRecord;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.RechargeRecordService;
import cn.lary.module.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RechargePayment extends AbstractBusinessPayment {


    private final PaymentPluginManager paymentPluginManager;
    private final WalletService walletService;
    private final RechargeRecordService rechargeRecordService;
    private final EventService eventService;
    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO dto) {
        return BusinessKit.ok();
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO rechargeDTO) {
        RechargeDTO dto = (RechargeDTO) rechargeDTO;
        long uid = RequestContext.getLoginUID();
        RechargeRecord data = transactionTemplate.execute(status -> {
            Wallet wallet = walletService.lambdaQuery()
                    .select(Wallet::getUid)
                    .eq(Wallet::getUid, uid)
                    .one();
            if (wallet == null) {
                return null;
            }
            BigDecimal amount = dto.getAmount();
            RechargeRecord rechargeRecord = new RechargeRecord()
                    .setPayway(dto.getPlugin())
                    .setAmount(amount)
                    .setStarNum(amount.longValue())
                    .setStatus(LARY.PAYMENT.STATUS.INIT);
            rechargeRecordService.save(rechargeRecord);
            RechargeEventDTO eventDTO = new RechargeEventDTO(uid, amount, rechargeRecord.getId());
            long eventId = eventService.begin(eventDTO);
            rechargeRecordService.lambdaUpdate()
                    .set(RechargeRecord::getEventId, eventId)
                    .eq(RechargeRecord::getId, rechargeRecord.getId());
            return rechargeRecord;
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
        rechargeRecordService.lambdaUpdate()
                .set(RechargeRecord::getStatus, LARY.PAYMENT.STATUS.FAIL)
                .set(RechargeRecord::getFailReason,vo.getErrMsg())
                .set(RechargeRecord::getCompleteAt, LocalDateTime.now())
                .eq(RechargeRecord::getId, vo.getPaymentId());
    }

    @Override
    protected void processWhenPaymentSuccess(PaymentBuildVO vo) {
        rechargeRecordService.lambdaUpdate()
                .set(RechargeRecord::getStatus, LARY.PAYMENT.STATUS.COMMIT)
                .eq(RechargeRecord::getId, vo.getPaymentId());
        messageService.asyncSendRocketMessage(new RechargeActiveDetectionMessage());
    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.RECHARGE;
    }
}
