package cn.lary.advertisement.dubbo;

import cn.lary.advertisement.component.AdExecute;
import cn.lary.api.advertisement.YutakAdService;
import cn.lary.api.advertisement.dto.ADPageQueryDTO;
import cn.lary.api.advertisement.dto.ADPaymentDTO;
import cn.lary.api.advertisement.dto.ProviderBuildDTO;
import cn.lary.api.advertisement.dto.ProviderUpdateDTO;
import cn.lary.api.advertisement.entity.Provider;
import cn.lary.api.advertisement.vo.AdvertisementVO;
import cn.lary.api.payment.vo.PaymentBuildVO;
import cn.lary.common.dto.PageResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@DubboService
public class YutakAdServiceImpl implements YutakAdService {

    @Autowired
    private AdExecute adExecute;

    @Override
    public String hello() {
        return "hello world";
    }
    @Override
    public SingleResponse<PaymentBuildVO> pay( ADPaymentDTO dto) {
        ResponsePair<PaymentBuildVO> response = adExecute.pay(dto);
        if(response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }
    @Override
    public PageResponse<AdvertisementVO> my(ADPageQueryDTO dto) {
        ResponsePair<List<AdvertisementVO>> response = adExecute.my(dto);
        if (response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),dto.getPageIndex(),dto.getPageSize());
    }

    @Override
    public ResponsePair<List<AdvertisementVO>> pageQuery(ADPageQueryDTO dto) {
        return adExecute.my(dto);
    }

    @Override
    public ResponsePair<PaymentBuildVO> payment(ADPaymentDTO dto) {
        return adExecute.pay(dto);
    }

    @Override
    public ResponsePair<Provider> addProvider(ProviderBuildDTO dto) {
        return adExecute.addProvider(dto);
    }

    @Override
    public ResponsePair<Void> updateProvider(ProviderUpdateDTO dto) {
        return adExecute.updateProvider(dto);
    }
}
