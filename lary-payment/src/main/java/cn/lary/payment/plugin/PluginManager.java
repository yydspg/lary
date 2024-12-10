package cn.lary.payment.plugin;

import cn.lary.api.payment.dto.PaymentNotifyProcessPair;
import cn.lary.api.payment.dto.PaymentQueryProcessPair;
import cn.lary.api.payment.vo.PaymentBuildVO;
import cn.lary.payment.component.PaymentProcessPair;

public interface PluginManager {

     PaymentBuildVO pay(PaymentProcessPair pair);
    /**
     * delegate
     */
     void doCallback(PaymentNotifyProcessPair pair);

    /**
     * delegate
     */
     void doNotify(PaymentNotifyProcessPair pair);

    /**
     * delegate
     * @param pair {@link PaymentQueryProcessPair}}
     */
    void doQuery(PaymentQueryProcessPair pair);

}
