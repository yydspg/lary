package cn.lary.module.pay.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PayBuildDTO {
    private boolean ok;
    private String outTradeNo;
    private String contentType;
    private String body;
    private String errCode;
    private String errMsg;
}
