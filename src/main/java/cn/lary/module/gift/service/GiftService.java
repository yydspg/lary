package cn.lary.module.gift.service;

import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.vo.GiftVO;
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
public interface GiftService extends IService<Gift> {

    List<GiftVO> getAllGiftVO();
}
