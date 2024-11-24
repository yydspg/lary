package cn.lary.invest.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.payment.component.AbstractBusinessPayment;
import cn.lary.payment.component.PaymentProcessPair;
import cn.lary.payment.dto.BusinessPaymentDTO;
import cn.lary.payment.vo.PaymentBuildVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestPaymentComponent  extends AbstractBusinessPayment {
    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO dto) {
        return null;
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO dto) {
        return BusinessKit.ok();
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
        return 0;
    }
}
