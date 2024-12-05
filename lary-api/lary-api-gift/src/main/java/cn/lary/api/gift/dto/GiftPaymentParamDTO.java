package cn.lary.api.gift.dto;


import cn.lary.api.payment.dto.PaymentParamDTO;
import cn.lary.common.constant.LARY;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GiftPaymentParamDTO extends PaymentParamDTO {


    public GiftPaymentParamDTO() {
        super.setBusinessSign(LARY.BUSINESS.PAYMENT.GIFT);
    }

}
