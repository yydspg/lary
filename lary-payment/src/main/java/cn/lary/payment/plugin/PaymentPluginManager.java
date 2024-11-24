package cn.lary.payment.plugin;

import cn.lary.payment.component.PaymentNotifyProcessPair;
import cn.lary.payment.component.PaymentProcessPair;
import cn.lary.payment.component.PaymentQueryProcessPair;
import cn.lary.payment.vo.PaymentBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;


@Slf4j
@Component
public class PaymentPluginManager {

    private final HashMap<Integer, PaymentPlugin> plugins = new HashMap<>();

    @Autowired
    public PaymentPluginManager(Collection<PaymentPlugin> paymentPlugins) {
        for (PaymentPlugin paymentPlugin : paymentPlugins) {
            plugins.putIfAbsent(paymentPlugin.getPaymentPlugin(), paymentPlugin);
        }
    }


    public PaymentBuildVO pay(PaymentProcessPair pair) {
        return plugins.get(pair.getPaymentPlugin()).doPay(pair);
    }
    /**
     * delegate
     */
    public void doCallback(PaymentNotifyProcessPair pair){
        plugins.get(pair.getPaymentPlugin()).doCallback(pair);
    }

    /**
     * delegate
     */
    public void doNotify(PaymentNotifyProcessPair pair){
        plugins.get(pair.getPaymentPlugin()).doNotify(pair);
    }

    /**
     * delegate
     * @param pair {@link PaymentQueryProcessPair}}
     */
    public void doQuery(PaymentQueryProcessPair pair){
       plugins.get(pair.getPlugin()).doQuery(pair);
    }

}
