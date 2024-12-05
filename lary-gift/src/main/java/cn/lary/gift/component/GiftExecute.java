package cn.lary.gift.component;

import cn.lary.api.payment.vo.PaymentBuildVO;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.api.gift.dto.GiftOrderDTO;
import cn.lary.api.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.gift.service.GiftOrderService;
import cn.lary.api.gift.vo.GiftOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiftExecute {


    private final GiftPayment giftPayment;
    private final String gifts = "";
    private final GiftOrderService giftOrderService;

    /**
     * 支付
     * @param dto {@link GiftOrderDTO}
     * @return {@link PaymentBuildVO}
     */
    public ResponsePair<PaymentBuildVO> pay(GiftOrderDTO dto) {
        return giftPayment.executePayment(dto);
    }

    /**
     * 返回所有礼物的 VO
     * @return gift collection json
     */
    public ResponsePair<String> gifts() {
            return BusinessKit.ok(gifts);
    }

    public ResponsePair<List<GiftOrderVO>> orders(GiftOrderPageQueryDTO dto) {
            return giftOrderService.my(dto);
    }
    /**
     * 初始化
     */
    @PostConstruct
    public void init(){

    }

}
