package cn.lary.module.pay.core;

import cn.lary.kit.CollectionKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class PayCallbackExecute {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            15, 20, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

        }
    });
    private final Map<Integer, PayCallback> map = new HashMap<>();

    @Autowired
    public PayCallbackExecute payCallbackExecute(List<PayCallback> callbacks) {
        if (CollectionKit.isEmpty(callbacks)) {
            log.error("callbacks is empty");
            return null;
        }
        callbacks.forEach(callback -> map.put(callback.getBiz(),callback));
        return this;
    }

    /**
     * 业务支付回调
     * @param args map
     * @param biz 业务code
     */
    public void onSuccess(Map<String,String> args,int biz,int payWay) {
        PayCallback callback = map.get(biz);
        if (callback == null) {
            log.error("on success callback error,invalid biz code:{}",biz);
            return;
        }
        executor.execute(()-> callback.onSuccess(args,payWay));
    }

    /**
     * 业务支付失败回调
     * @param args map
     * @param biz 业务code
     */
    public void onFail(Map<String,String> args,int biz,int payWay) {
        PayCallback callback = map.get(biz);
        if (callback == null) {
            log.error("on fail callback error,invalid biz code:{}",biz);
            return;
        }
        executor.execute(()->callback.onFail(args,payWay));
    }
}
