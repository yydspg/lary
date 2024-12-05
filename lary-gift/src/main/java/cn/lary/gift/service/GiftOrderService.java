package cn.lary.gift.service;

import cn.lary.api.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.api.gift.entity.GiftOrder;
import cn.lary.api.gift.vo.GiftOrderVO;
import cn.lary.common.dto.ResponsePair;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface GiftOrderService extends IService<GiftOrder> {

   ResponsePair<List<GiftOrderVO>> my(GiftOrderPageQueryDTO dto);


}
