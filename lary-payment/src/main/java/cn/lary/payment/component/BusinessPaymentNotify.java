package cn.lary.payment.component;

import cn.lary.common.business.BusinessSign;
import cn.lary.api.payment.dto.PaymentNotifyProcessPair;
import cn.lary.api.payment.vo.PaymentQueryVO;

public abstract class BusinessPaymentNotify<T> implements BusinessSign {

    public final void onSuccess(PaymentNotifyProcessPair pair) {
        processSuccess(pair,null);
    }


    public final void onFail(PaymentNotifyProcessPair pair) {
        processFail(pair,null);
    }


    public final void onQuerySuccess(PaymentQueryVO pair) {
        processSuccess(null,pair);
    }


    public final void onQueryFail(PaymentQueryVO pair) {
        processFail(null,pair);
    }

    public final void  processSuccess(PaymentNotifyProcessPair pair,PaymentQueryVO data){
        whenSuccess(getPaymentNotify(pair, data));
    }

    public final void  processFail(PaymentNotifyProcessPair pair,PaymentQueryVO data){
         whenFail(getPaymentNotify(pair,data));
    }

    public abstract void whenSuccess(T data);
    public abstract void whenFail(T data);
    public abstract T getPaymentNotify(PaymentNotifyProcessPair pair,PaymentQueryVO data);
}
