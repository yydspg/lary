package cn.lary.payment.component;

import cn.lary.common.exception.SystemException;
import cn.lary.common.kit.CollectionKit;
import cn.lary.payment.vo.PaymentQueryVO;
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
     * 服务商异步通知,支付成功处理
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
     * 服务商异步通知,支付失败处理
     */
    public void processFail(PaymentNotifyProcessPair pair) {
        BusinessPaymentNotify notify = map.get(pair.getBusinessSign());
        if (notify == null) {
            log.error("on FAIL callback error,invalid business code:{}",pair.getBusinessSign());
            return;
        }
        executor.execute(()->notify.onFail(pair));
    }

    /**
     * 主动查询,支付成功处理
     * @param vo {@link PaymentQueryVO}
     */
    public void processQuerySuccess(PaymentQueryVO vo) {
        BusinessPaymentNotify notify = map.get(vo.getPair().getBusinessSign());
        if (notify == null) {
            log.error("on query success process ,invalid business code:{}",vo.getPair().getBusinessSign());
            return;
        }
        executor.execute(()->{notify.onQuerySuccess(vo);});
    }
    /**
     * 主动查询,支付失败处理
     * @param vo {@link PaymentQueryVO}
     */
    public void processQueryFail(PaymentQueryVO vo) {
        BusinessPaymentNotify notify = map.get(vo.getPair().getBusinessSign());
        if (notify == null) {
            log.error("on query fail process ,invalid business code:{}",vo.getPair().getBusinessSign());
            return;
        }
        executor.execute(()->{notify.onQueryFail(vo);});
    }
}
