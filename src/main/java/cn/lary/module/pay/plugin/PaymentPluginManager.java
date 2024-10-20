package cn.lary.module.pay.plugin;

import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.pay.dto.PaymentParamDTO;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.service.PaymentLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


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

}
