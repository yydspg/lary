package cn.lary.module.gift.serviceImpl;

import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.mapper.GiftOrderMapper;
import cn.lary.module.gift.service.GiftOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@Service
@RequiredArgsConstructor
public class GiftOrderServiceImpl extends ServiceImpl<GiftOrderMapper, GiftOrder> implements GiftOrderService {

    private final GiftOrderMapper giftOrderMapper;
    @Override
    public long collectCostMoneyByGiftChannelId(String giftChannelId) {
        return giftOrderMapper.getGiftBuyRecordCountByGiftId(giftChannelId);
    }
}
