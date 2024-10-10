package cn.lary.module.gift.api;

import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.gift.core.GiftBizExecute;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.module.gift.vo.GiftOrderVO;
import cn.lary.module.pay.vo.PayBuildVO;
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

    private final GiftBizExecute giftBizExecute;

    /**
     * 购买礼物
     * @param req {@link GiftOrderDTO}
     * @return {@link PayBuildVO}
     */
    @PostMapping(value = "/pay")
    public SingleResponse<PayBuildVO> pay(@RequestBody GiftOrderDTO req) {
        ResPair<PayBuildVO> res = giftBizExecute.pay(req);
        if(!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 查询所有礼物信息
     * @return JSON 字符串
     */
    @GetMapping("")
    public SingleResponse<String> allGifts() {
        ResPair<String> giftVOs = giftBizExecute.getGifts();
        if(!giftVOs.isOk()) {
            return ResponseKit.fail(giftVOs.getMsg());
        }
        return ResponseKit.ok(giftVOs.getData());
    }

    /**
     * 查询礼物购买订单
     * @param req {@link GiftOrderPageQueryDTO}
     * @return {@link GiftOrderVO}
     */
    @PostMapping("/orders")
    public PageResponse<GiftOrderVO> orders(@RequestBody @Valid GiftOrderPageQueryDTO req) {
        ResPair<List<GiftOrderVO>> res = giftBizExecute.orders(req);
        if(!res.isOk()) {
            return ResponseKit.pageFail(res.getMsg());
        }
        return ResponseKit.pageOk(res.getData(),req.getPageIndex(),req.getPageSize());
    }

}
