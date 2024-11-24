package cn.lary.wallet.component;

import cn.lary.api.message.YutakMessageService;
import cn.lary.common.constant.LARY;
import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.payment.component.AbstractBusinessPayment;
import cn.lary.payment.component.PaymentProcessPair;
import cn.lary.payment.constant.PAYMENT;
import cn.lary.payment.dto.BusinessPaymentDTO;
import cn.lary.payment.dto.PaymentParamDTO;
import cn.lary.payment.plugin.PaymentPluginManager;
import cn.lary.payment.vo.PaymentBuildVO;
import cn.lary.wallet.dto.RechargeDTO;
import cn.lary.wallet.entity.RechargeRecord;
import cn.lary.wallet.entity.Wallet;
import cn.lary.wallet.service.RechargeRecordService;
import cn.lary.wallet.service.WalletService;
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
//    private final EventService eventService;
    private final YutakMessageService messageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO dto) {
        return BusinessKit.ok();
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO rechargeDTO) {
        RechargeDTO dto = (RechargeDTO) rechargeDTO;
        long uid = RequestContext.uid();
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
                    .setStatus(PAYMENT.STATUS.INIT);
            rechargeRecordService.save(rechargeRecord);
//            RechargeEventDTO eventDTO = new RechargeEventDTO(uid, amount, rechargeRecord.getId());
//            long eventId = eventService.begin(eventDTO);
            rechargeRecordService.lambdaUpdate()
//                    .set(RechargeRecord::getEid, eventId)
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
                .set(RechargeRecord::getStatus, PAYMENT.STATUS.FAIL)
                .set(RechargeRecord::getFailReason,vo.getErrMsg())
                .set(RechargeRecord::getCompleteAt, LocalDateTime.now())
                .eq(RechargeRecord::getId, vo.getPaymentId());
    }

    @Override
    protected void processWhenPaymentSuccess(PaymentBuildVO vo) {
        rechargeRecordService.lambdaUpdate()
                .set(RechargeRecord::getStatus, PAYMENT.STATUS.COMMIT)
                .eq(RechargeRecord::getId, vo.getPaymentId());
//        messageService.asyncSendRocketMessage(new RechargeActiveDetectionMessage());
    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.RECHARGE;
    }
}
