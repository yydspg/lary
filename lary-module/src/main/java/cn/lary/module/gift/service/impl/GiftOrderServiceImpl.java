package cn.lary.module.gift.service.impl;

import cn.lary.module.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.mapper.GiftOrderMapper;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.gift.vo.GiftOrderVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class GiftOrderServiceImpl extends ServiceImpl<GiftOrderMapper, GiftOrder> implements GiftOrderService {

    private final GiftOrderMapper giftOrderMapper;

    @Override
    public List<GiftOrderVO> queryGiftOrders(GiftOrderPageQueryDTO dto) {
        if (dto == null || dto.getUid() <= 0) {
            return null;
        }
        return giftOrderMapper.getGiftOrderVOs(dto);
    }


}
