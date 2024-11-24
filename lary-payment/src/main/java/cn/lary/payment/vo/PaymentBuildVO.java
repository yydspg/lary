package cn.lary.payment.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentBuildVO {

    private boolean fail;

    private long paymentId;

    private String outTradeNo;

    private String contentType;

    private String body;

    private String errCode;

    private String errMsg;
}
