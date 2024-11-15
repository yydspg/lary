package cn.lary.module.order.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.pay.component.AbstractBusinessPayment;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import cn.lary.module.pay.plugin.PaymentPluginManager;
import cn.lary.module.pay.vo.PaymentBuildVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoodsOrderPayment extends AbstractBusinessPayment {

    private final PaymentPluginManager paymentPluginManager;
    private final TransactionTemplate transactionTemplate;
    private final MessageService messageService;

    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO dto) {
        return null;
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO dto) {
        return null;
    }

    @Override
    protected void afterPay(BusinessPaymentDTO dto) {

    }

    @Override
    protected PaymentBuildVO doPay(PaymentProcessPair pair) {
        return null;
    }

    @Override
    protected void processWhenPaymentFail(PaymentBuildVO vo) {

    }

    @Override
    protected void processWhenPaymentSuccess(PaymentBuildVO vo) {

    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.ORDER;
    }
}
