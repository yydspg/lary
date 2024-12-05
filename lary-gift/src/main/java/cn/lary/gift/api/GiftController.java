package cn.lary.gift.api;

import cn.lary.api.payment.vo.PaymentBuildVO;
import cn.lary.common.dto.PageResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.api.gift.dto.GiftOrderDTO;
import cn.lary.api.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.api.gift.vo.GiftOrderVO;
import cn.lary.gift.component.GiftExecute;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  礼物模块
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@RestController
@RequestMapping("/v1/gift/")
@RequiredArgsConstructor
public class GiftController {

    private final GiftExecute giftExecute;

    /**
     * 购买礼物
     * @param dto {@link GiftOrderDTO}
     * @return {@link PaymentBuildVO}
     */
    @PostMapping(value = "/pay")
    public SingleResponse<PaymentBuildVO> pay(@RequestBody GiftOrderDTO dto) {
        ResponsePair<PaymentBuildVO> response = giftExecute.pay(dto);
        if(response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 查询所有礼物信息
     * @return JSON 字符串
     */
    @GetMapping("")
    public SingleResponse<String> gifts() {
        ResponsePair<String> pair = giftExecute.gifts();
        if(pair.isFail()) {
            return ResponseKit.fail(pair.getMsg());
        }
        return ResponseKit.ok(pair.getData());
    }

    /**
     * 查询礼物购买订单
     * @param dto {@link GiftOrderPageQueryDTO}
     * @return {@link GiftOrderVO}
     */
    @PostMapping("/orders")
    public PageResponse<GiftOrderVO> orders(@RequestBody @Valid GiftOrderPageQueryDTO dto) {
        ResponsePair<List<GiftOrderVO>> response = giftExecute.orders(dto);
        if(response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),dto.getPageIndex(),dto.getPageSize());
    }

}
