package cn.lary.module.gift.serviceImpl;

import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.mapper.GiftMapper;
import cn.lary.module.gift.service.GiftService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Service
public class GiftServiceImpl extends ServiceImpl<GiftMapper, Gift> implements GiftService {

}
