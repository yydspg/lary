package cn.lary.module.gift.service;

import cn.lary.module.gift.entity.GiftOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface GiftOrderService extends IService<GiftOrder> {

    long collectCostMoneyByGiftChannelId(String giftChannelId);
}
