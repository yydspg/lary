package cn.lary.gift.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.gift.entity.Gift;
import cn.lary.gift.vo.GiftCollectionVO;
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

    ResponsePair<List<GiftCollectionVO>> gifts();
}
