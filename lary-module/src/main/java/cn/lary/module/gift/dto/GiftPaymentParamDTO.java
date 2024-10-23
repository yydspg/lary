package cn.lary.module.gift.dto;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.dto.PaymentParamDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GiftPaymentParamDTO extends PaymentParamDTO {


    public GiftPaymentParamDTO() {
        super.setBusinessSign(LARY.BUSINESS.PAYMENT.GIFT);
    }

}
