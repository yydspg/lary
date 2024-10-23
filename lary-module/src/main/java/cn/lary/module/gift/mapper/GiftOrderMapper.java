package cn.lary.module.gift.mapper;

import cn.lary.module.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.vo.GiftOrderVO;
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


    List<GiftOrderVO> getGiftOrderVOs(GiftOrderPageQueryDTO dto);
}
