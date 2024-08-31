package cn.lary.module.user.serviceImpl;

import cn.lary.module.user.entity.UserRedDot;
import cn.lary.module.user.mapper.UserRedDotMapper;
import cn.lary.module.user.service.UserRedDotService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
public class UserRedDotServiceImpl extends ServiceImpl<UserRedDotMapper, UserRedDot> implements UserRedDotService {

    @Override
    public UserRedDot getUserRedDot(String uid, String category) {
        LambdaQueryWrapper<UserRedDot> qw = new LambdaQueryWrapper<UserRedDot>().eq(UserRedDot::getUid, uid).eq(UserRedDot::getCategory, category);
        return baseMapper.selectOne(qw,false);
    }
}
