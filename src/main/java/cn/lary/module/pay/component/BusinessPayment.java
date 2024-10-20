package cn.lary.module.pay.component;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import cn.lary.module.pay.vo.PaymentBuildVO;

public interface BusinessPayment {

   ResponsePair<PaymentBuildVO> executePayment(BusinessPaymentDTO dto);
}
