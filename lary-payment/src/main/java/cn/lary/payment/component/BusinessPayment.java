package cn.lary.payment.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.payment.dto.BusinessPaymentDTO;
import cn.lary.api.payment.vo.PaymentBuildVO;

public interface BusinessPayment {

   ResponsePair<PaymentBuildVO> executePayment(BusinessPaymentDTO dto);
}
