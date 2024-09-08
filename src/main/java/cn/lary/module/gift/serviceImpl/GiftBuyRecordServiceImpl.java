package cn.lary.module.gift.serviceImpl;

import cn.lary.module.gift.entity.GiftBuyRecord;
import cn.lary.module.gift.mapper.GiftBuyRecordMapper;
import cn.lary.module.gift.service.GiftBuyRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class GiftBuyRecordServiceImpl extends ServiceImpl<GiftBuyRecordMapper, GiftBuyRecord> implements GiftBuyRecordService {

    private final GiftBuyRecordMapper giftBuyRecordMapper;
    @Override
    public long collectCostMoneyByGiftChannelId(String giftChannelId) {
        return giftBuyRecordMapper.getGiftBuyRecordCountByGiftId(giftChannelId);
    }
}
