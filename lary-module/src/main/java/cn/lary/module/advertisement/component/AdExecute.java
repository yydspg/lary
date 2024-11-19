package cn.lary.module.advertisement.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.advertisement.dto.ADPaymentDTO;
import cn.lary.module.advertisement.service.AdvertisementService;
import cn.lary.module.advertisement.dto.ADPageQueryDTO;
import cn.lary.module.advertisement.vo.AdvertisementVO;
import cn.lary.module.pay.vo.PaymentBuildVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdExecute {

    private final ADPaymentComponent adPaymentComponent;
    private final AdvertisementService advertisementService;

    public ResponsePair<List<AdvertisementVO>>  my(ADPageQueryDTO dto){
        return advertisementService.my(dto);
    }

    public ResponsePair<PaymentBuildVO> pay(ADPaymentDTO dto){
        return adPaymentComponent.executePayment(dto);
    }
}
