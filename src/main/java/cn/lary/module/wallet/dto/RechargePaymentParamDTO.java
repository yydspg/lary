package cn.lary.module.wallet.dto;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.dto.PaymentParamDTO;
import lombok.Data;

import java.util.Map;
@Data
public class RechargePaymentParamDTO extends PaymentParamDTO {


    public RechargePaymentParamDTO() {
        super.setBusinessSign(LARY.PayBiz.recharge);
    }
}
