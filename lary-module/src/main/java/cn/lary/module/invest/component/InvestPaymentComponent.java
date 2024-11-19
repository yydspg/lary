package cn.lary.module.invest.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.pay.component.AbstractBusinessPayment;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import cn.lary.module.pay.vo.PaymentBuildVO;
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
