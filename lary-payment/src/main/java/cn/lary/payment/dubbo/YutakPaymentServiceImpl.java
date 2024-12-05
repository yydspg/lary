package cn.lary.payment.dubbo;

import cn.lary.api.payment.YutakPaymentService;
import cn.lary.payment.service.PaymentLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DubboService
@RequiredArgsConstructor
public class YutakPaymentServiceImpl implements YutakPaymentService {

    private final PaymentLogService paymentLogService;


}
