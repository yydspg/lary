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
public class GoodsOrderPaymentNotify implements BusinessPaymentNotify {

    @Override
    public void onSuccess(PaymentNotifyProcessPair pair) {

    }

    @Override
    public void onFail(PaymentNotifyProcessPair pair) {

    }

    @Override
    public void onQuerySuccess(PaymentQueryVO pair) {

    }

    @Override
    public void onQueryFail(PaymentQueryVO pair) {

    }

    @Override
    public int getSign() {
        return 0;
    }
}
