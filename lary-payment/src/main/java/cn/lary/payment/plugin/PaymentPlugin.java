package cn.lary.payment.plugin;

import cn.lary.api.payment.dto.PaymentNotifyProcessPair;
import cn.lary.payment.component.PaymentProcessPair;
import cn.lary.api.payment.dto.PaymentQueryProcessPair;
import cn.lary.api.payment.vo.PaymentBuildVO;

/**
 * 不同 支付服务商 支付需提供的支付逻辑
 */
public interface PaymentPlugin {

    void doNotify(PaymentNotifyProcessPair pair);

    void doCallback(PaymentNotifyProcessPair pair);

    PaymentBuildVO doPay(PaymentProcessPair pair);

    int getPaymentPlugin();

    void doQuery(PaymentQueryProcessPair pair);
}
