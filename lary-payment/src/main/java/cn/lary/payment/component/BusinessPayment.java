package cn.lary.payment.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.payment.dto.BusinessPaymentDTO;
import cn.lary.payment.vo.PaymentBuildVO;

public interface BusinessPayment {

   ResponsePair<PaymentBuildVO> executePayment(BusinessPaymentDTO dto);
}
