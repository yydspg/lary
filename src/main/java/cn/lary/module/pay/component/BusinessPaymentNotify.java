package cn.lary.module.pay.component;

import cn.lary.core.business.BusinessSign;

public interface BusinessPaymentNotify extends BusinessSign {

    /**
     * 成功后回调
     */
    void onSuccess(PaymentNotifyProcessPair pair);

    /**
     * 失败后回调
     */
    void onFail(PaymentNotifyProcessPair pair);

}
