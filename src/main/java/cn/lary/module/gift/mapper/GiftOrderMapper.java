package cn.lary.module.gift.mapper;

import cn.lary.module.gift.entity.GiftOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface GiftOrderMapper extends BaseMapper<GiftOrder> {

    long getGiftBuyRecordCountByGiftId(String giftChannelId);
}