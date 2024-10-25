package cn.lary.module.pay.component;

import cn.lary.common.business.BusinessSign;
import cn.lary.module.pay.vo.PaymentQueryVO;

public interface BusinessPaymentNotify extends BusinessSign {

    /**
     * 成功后业务回调
     */
    void onSuccess(PaymentNotifyProcessPair pair);

    /**
     * 失败后业务回调
     */
    void onFail(PaymentNotifyProcessPair pair);

    /**
     * 主动查询,订单失败处理
     */
    void onQuerySuccess(PaymentQueryVO pair);

    /**
     * 主动查询,订单成功处理
     */
    void onQueryFail(PaymentQueryVO pair);


}
