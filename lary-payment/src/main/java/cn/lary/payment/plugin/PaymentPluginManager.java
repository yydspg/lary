package cn.lary.payment.plugin;

import cn.lary.api.payment.dto.PaymentNotifyProcessPair;
import cn.lary.payment.component.PaymentProcessPair;
import cn.lary.api.payment.dto.PaymentQueryProcessPair;
import cn.lary.api.payment.vo.PaymentBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;


@Slf4j
@Component
public class PaymentPluginManager implements PluginManager {

    private final HashMap<Integer, PaymentPlugin> plugins = new HashMap<>();

    @Autowired
    public PaymentPluginManager(Collection<PaymentPlugin> paymentPlugins) {
        for (PaymentPlugin paymentPlugin : paymentPlugins) {
            plugins.putIfAbsent(paymentPlugin.getPaymentPlugin(), paymentPlugin);
        }
    }

    @Override
    public PaymentBuildVO pay(PaymentProcessPair pair) {
        return plugins.get(pair.getPaymentPlugin()).doPay(pair);
    }

    @Override
    public void doCallback(PaymentNotifyProcessPair pair){
        plugins.get(pair.getPaymentPlugin()).doCallback(pair);
    }

    @Override
    public void doNotify(PaymentNotifyProcessPair pair){
        plugins.get(pair.getPaymentPlugin()).doNotify(pair);
    }

    @Override
    public void doQuery(PaymentQueryProcessPair pair){
       plugins.get(pair.getPlugin()).doQuery(pair);
    }

}
