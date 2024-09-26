package cn.lary.module.pay.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PayBuildVO {
    private boolean ok;
    private String outTradeNo;
    private String contentType;
    private String body;
    private String errCode;
    private String errMsg;
}
