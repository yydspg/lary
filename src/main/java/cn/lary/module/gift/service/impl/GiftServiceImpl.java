package cn.lary.module.gift.service.impl;

import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.mapper.GiftMapper;
import cn.lary.module.gift.service.GiftService;
import cn.lary.module.gift.vo.GiftVO;
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
public class GiftServiceImpl extends ServiceImpl<GiftMapper, Gift> implements GiftService {
    private final GiftMapper giftMapper;

    @Override
    public List<GiftVO> getAllGiftVO() {
        return giftMapper.queryAll();
    }
}
