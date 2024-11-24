package cn.lary.advertisement.api;

import cn.lary.advertisement.component.AdExecute;
import cn.lary.advertisement.dto.ADPageQueryDTO;
import cn.lary.advertisement.dto.ADPaymentDTO;
import cn.lary.advertisement.vo.AdvertisementVO;
import cn.lary.common.dto.PageResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.payment.vo.PaymentBuildVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
@Slf4j
@RestController
@RequestMapping("/v1/ad")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdExecute adExecute;

    /**
     * 创建广告投放事件
     * @param dto {@link ADPaymentDTO}
     * @return ok
     */
    @PostMapping(value = "/pay")
    public SingleResponse<PaymentBuildVO> pay(@RequestBody ADPaymentDTO dto) {
        ResponsePair<PaymentBuildVO> response = adExecute.pay(dto);
        if(response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 分页查询广告投放记录
     * @param dto {@link ADPageQueryDTO}
     * @return {@link AdvertisementVO}
     */
    public PageResponse<AdvertisementVO> my(ADPageQueryDTO dto) {
        ResponsePair<List<AdvertisementVO>> response = adExecute.my(dto);
        if (response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),dto.getPageIndex(),dto.getPageSize());
    }
}
