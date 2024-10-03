package cn.lary.module.gift.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.gift.core.GiftBizExecute;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.service.GiftService;
import cn.lary.module.pay.vo.PayBuildVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
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
        int uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        ResPair<PayBuildVO> res = giftBizExecute.pay(uid,uidName, req);
        if(!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }

}
