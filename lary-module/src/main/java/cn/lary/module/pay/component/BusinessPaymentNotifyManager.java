package cn.lary.module.pay.component;

import cn.lary.common.exception.SystemException;
import cn.lary.common.kit.CollectionKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessPaymentNotifyManager {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            15, 20, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

        }
    });

    private final Map<Integer, BusinessPaymentNotify> map = new HashMap<>();

    @Autowired
    public BusinessPaymentNotifyManager(Collection<BusinessPaymentNotify> callbacks) {
        if (CollectionKit.isEmpty(callbacks)) {
            log.error("business callbacks empty");
            throw new SystemException("business callbacks empty");
        }
        callbacks.forEach(callback -> map.put(callback.getSign(),callback));
    }

    /**
     * 业务支付回调
     */
    public void processSuccess(PaymentNotifyProcessPair pair) {
        BusinessPaymentNotify callback = map.get(pair.getBusinessSign());
        if (callback == null) {
            log.error("on success callback error,invalid business code:{}",pair.getBusinessSign());
            return;
        }
        executor.execute(()-> callback.onSuccess(pair));
    }

    /**
     * 业务支付失败回调
     */
    public void processFail(PaymentNotifyProcessPair pair) {
        BusinessPaymentNotify callback = map.get(pair.getBusinessSign());
        if (callback == null) {
            log.error("on fail callback error,invalid business code:{}",pair.getBusinessSign());
            return;
        }
        executor.execute(()->callback.onFail(pair));
    }
}
