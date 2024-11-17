package cn.lary.module.pay.vo;

import cn.lary.module.pay.component.PaymentQueryProcessPair;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class PaymentQueryVO {

    private String sn;

    private long paymentId;

    private String status;

    private String reason;

    private boolean executeFail;

    private BigDecimal amount;

    private PaymentQueryProcessPair pair;
}
