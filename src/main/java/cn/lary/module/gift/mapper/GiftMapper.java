package cn.lary.module.gift.mapper;

import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.vo.GiftVO;
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
public interface GiftMapper extends BaseMapper<Gift> {

    List<GiftVO> queryAll();
}
