package cn.lary.gift.service.impl;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.gift.dto.GiftOrderPageQueryDTO;
import cn.lary.gift.entity.GiftOrder;
import cn.lary.gift.mapper.GiftOrderMapper;
import cn.lary.gift.service.GiftOrderService;
import cn.lary.gift.vo.GiftOrderVO;
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
    public ResponsePair<List<GiftOrderVO>> my(GiftOrderPageQueryDTO dto) {
        if (dto == null || dto.getUid() <= 0) {
            return BusinessKit.fail("invalid param");
        }
        return BusinessKit.ok(giftOrderMapper.orders(dto));
    }


}
