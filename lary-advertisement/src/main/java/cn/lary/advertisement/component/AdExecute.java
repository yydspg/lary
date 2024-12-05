package cn.lary.advertisement.component;

import cn.lary.api.advertisement.dto.ADPageQueryDTO;
import cn.lary.api.advertisement.dto.ADPaymentDTO;
import cn.lary.advertisement.service.AdvertisementService;
import cn.lary.api.advertisement.vo.AdvertisementVO;
import cn.lary.api.payment.vo.PaymentBuildVO;
import cn.lary.common.dto.ResponsePair;

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
