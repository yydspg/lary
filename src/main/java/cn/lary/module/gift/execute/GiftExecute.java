package cn.lary.module.gift.execute;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.JSONKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.gift.dto.GiftOrderDTO;
import cn.lary.module.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.entity.GiftType;
import cn.lary.module.gift.service.AnchorTurnoverService;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.service.GiftService;
import cn.lary.module.gift.service.GiftTypeService;
import cn.lary.module.gift.vo.GiftCollectionVO;
import cn.lary.module.gift.vo.GiftOrderVO;
import cn.lary.module.gift.vo.GiftVO;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.wallet.service.WalletOutcomeService;
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
    //component
    private final HashMap<Integer, GiftCollectionVO> giftCollections = new HashMap<>();
    private String giftCollectionJSON;

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
    public ResponsePair<String> getGifts() {
        return BusinessKit.ok(giftCollectionJSON);
    }


    /**
     * 初始化
     */
    @PostConstruct
    public void init(){

    }

}
