package cn.lary.gift.dto;


import cn.lary.common.constant.LARY;
import cn.lary.payment.dto.PaymentParamDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GiftPaymentParamDTO extends PaymentParamDTO {


    public GiftPaymentParamDTO() {
        super.setBusinessSign(LARY.BUSINESS.PAYMENT.GIFT);
    }

}
