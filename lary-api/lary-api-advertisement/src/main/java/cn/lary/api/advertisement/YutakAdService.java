package cn.lary.api.advertisement;

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
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.ResponseKit;
import cn.lary.common.kit.StringKit;
import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.message.MediaType;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.apache.dubbo.remoting.http12.rest.Param;
import org.apache.dubbo.remoting.http12.rest.ParamType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.List;

public interface YutakAdService {

    @Mapping(path = "/hi", method = HttpMethods.GET)
    String hello();

    /**
     * 广告支付
     * @param dto {@link ADPaymentDTO}
     * @return {@link PaymentBuildVO}
     */
    @PostMapping(path = "/pay")
    SingleResponse<PaymentBuildVO> pay(@RequestBody ADPaymentDTO dto);

    /**
     * 分页查询广告投放记录
     * @param dto {@link ADPageQueryDTO}
     * @return {@link AdvertisementVO}
     */
    @PostMapping(path = "/my")
    PageResponse<AdvertisementVO> my(@RequestBody ADPageQueryDTO dto);


    ResponsePair<List<AdvertisementVO>>  pageQuery(ADPageQueryDTO dto);

    ResponsePair<PaymentBuildVO> payment(ADPaymentDTO dto);

    ResponsePair<Provider> addProvider(ProviderBuildDTO dto);

    ResponsePair<Void> updateProvider(ProviderUpdateDTO dto);
}
