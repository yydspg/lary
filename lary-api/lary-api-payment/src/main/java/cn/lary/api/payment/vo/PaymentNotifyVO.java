package cn.lary.api.payment.vo;

import cn.lary.api.payment.dto.PaymentNotifyProcessPair;

public interface PaymentNotifyVO {

    PaymentNotifyVO of(PaymentNotifyProcessPair pair);

}
