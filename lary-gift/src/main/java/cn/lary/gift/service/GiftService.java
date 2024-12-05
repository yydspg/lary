package cn.lary.gift.service;

import cn.lary.api.gift.entity.Gift;
import cn.lary.api.gift.vo.GiftCollectionVO;
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
public interface GiftService extends IService<Gift> {

    ResponsePair<List<GiftCollectionVO>> gifts();
}
