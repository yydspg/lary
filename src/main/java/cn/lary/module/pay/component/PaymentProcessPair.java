package cn.lary.module.pay.component;

import cn.lary.module.pay.dto.PaymentParamDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public  class PaymentProcessPair {

    private PaymentParamDTO param;

    private int paymentPlugin;

    private int paymentWay;

    private boolean isInternal;
}
