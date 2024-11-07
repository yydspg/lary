package cn.lary.module.gift.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.service.GiftService;
import cn.lary.module.gift.vo.GiftCollectionVO;
import cn.lary.module.gift.vo.GiftOrderVO;
import cn.lary.module.pay.vo.PaymentBuildVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
    @Transactional(rollbackFor = Exception.class)
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
