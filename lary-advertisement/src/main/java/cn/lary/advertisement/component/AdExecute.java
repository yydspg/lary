package cn.lary.advertisement.component;

import cn.lary.advertisement.service.ProviderService;
import cn.lary.api.advertisement.dto.ADPageQueryDTO;
import cn.lary.api.advertisement.dto.ADPaymentDTO;
import cn.lary.advertisement.service.AdvertisementService;
import cn.lary.api.advertisement.dto.ProviderBuildDTO;
import cn.lary.api.advertisement.dto.ProviderUpdateDTO;
import cn.lary.api.advertisement.entity.Provider;
import cn.lary.api.advertisement.vo.AdvertisementVO;
import cn.lary.api.payment.vo.PaymentBuildVO;
import cn.lary.common.dto.ResponsePair;

import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.StringKit;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdExecute {

    private final ADPaymentComponent adPaymentComponent;
    private final AdvertisementService advertisementService;
    private final ProviderService providerService;

    public ResponsePair<List<AdvertisementVO>>  my(ADPageQueryDTO dto){
        if (dto == null){
            return BusinessKit.fail("data empty");
        }
        if(dto.getPid() == null){
            return BusinessKit.fail("data pid empty");
        }
        return advertisementService.my(dto);
    }

    public ResponsePair<PaymentBuildVO> pay(ADPaymentDTO dto){
        if(dto.getPid() == null){
            return BusinessKit.fail("data pid empty");
        }
        return adPaymentComponent.executePayment(dto);
    }

    public ResponsePair<Provider> addProvider(ProviderBuildDTO dto){
        if(dto == null){
            return BusinessKit.fail("data empty");
        }
        if(dto.getName() == null){
            return BusinessKit.fail("data name empty");
        }
        if (SensitiveWordHelper.contains(dto.getName())){
            return BusinessKit.fail("data name contains sensitive word");
        }
        if(dto.getAmount().compareTo(BigDecimal.ZERO) < 0){
            return BusinessKit.fail("data amount less than 0");
        }
        return providerService.build(dto);
    }

    public ResponsePair<Void> updateProvider(ProviderUpdateDTO dto){
        if(dto.getPid() == null){
            return BusinessKit.fail("data pid empty");
        }
        if (StringKit.isEmpty(dto.getName())){
            return BusinessKit.fail("data name empty");
        }
        if (dto.getStatus() == null){
            return BusinessKit.fail("data status empty");
        }
        return providerService.update(dto);
    }
}
