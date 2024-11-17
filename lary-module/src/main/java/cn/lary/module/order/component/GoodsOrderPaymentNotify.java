package cn.lary.module.order.component;

import cn.lary.module.pay.component.BusinessPaymentNotify;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.vo.PaymentQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoodsOrderPaymentNotify extends BusinessPaymentNotify {


    @Override
    public void whenSuccess(Object data) {

    }

    @Override
    public void whenFail(Object data) {

    }

    @Override
    public Object getPaymentNotify(PaymentNotifyProcessPair pair, PaymentQueryVO data) {
        return null;
    }

    @Override
    public int getSign() {
        return 0;
    }
}
