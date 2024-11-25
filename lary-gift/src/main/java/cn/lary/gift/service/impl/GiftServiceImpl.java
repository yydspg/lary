package cn.lary.gift.service.impl;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.gift.component.GiftCacheComponent;
import cn.lary.gift.entity.Gift;
import cn.lary.gift.mapper.GiftMapper;
import cn.lary.gift.service.GiftService;
import cn.lary.gift.vo.GiftCollectionVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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

    private final GiftCacheComponent giftCacheComponent;
    private final List<Integer> types = new ArrayList<>();

    @Override
    public ResponsePair<List<GiftCollectionVO>> gifts() {
        return BusinessKit.ok(
            types.stream()
                    .map(giftCacheComponent::getCollection)
                    .map(GiftCollectionVO::new)
                    .toList());
    }

    @PostConstruct
    public void init(){
        for (int i = 1; i < 5; i++) {
            types.add(i+10000);
        }
    }
}
