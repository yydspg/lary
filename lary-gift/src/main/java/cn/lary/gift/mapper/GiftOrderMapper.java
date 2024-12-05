package cn.lary.gift.mapper;


import cn.lary.api.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.api.gift.entity.GiftOrder;
import cn.lary.api.gift.vo.GiftOrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface GiftOrderMapper extends BaseMapper<GiftOrder> {


    List<GiftOrderVO> orders(GiftOrderPageQueryDTO dto);

}
