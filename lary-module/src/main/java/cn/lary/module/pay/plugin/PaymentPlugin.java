package cn.lary.module.pay.plugin;

import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.component.PaymentQueryProcessPair;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.pay.vo.PaymentQueryVO;

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
