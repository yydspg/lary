package cn.lary.module.user.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.kit.CollectionKit;
import cn.lary.module.user.entity.UserRedDot;
import cn.lary.module.user.mapper.UserRedDotMapper;
import cn.lary.module.user.service.UserRedDotService;
import cn.lary.module.user.vo.UserRedDotVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Slf4j
@Service
public class UserRedDotServiceImpl extends ServiceImpl<UserRedDotMapper, UserRedDot> implements UserRedDotService {


    @Override
    public ResponsePair<List<UserRedDotVO>> redDots() {
        List<UserRedDot> data = lambdaQuery()
                .select(UserRedDot::getCategory, UserRedDot::getCount, UserRedDot::getIsDot)
                .eq(UserRedDot::getUid, RequestContext.getLoginUID())
                .orderByDesc(UserRedDot::getCreateAt)
                .list();
        if (CollectionKit.isEmpty(data)) {
            log.error("search red dots failed,uid:{}",RequestContext.getLoginUID());
            return BusinessKit.fail("search red dots failed");
        }
        return BusinessKit.ok(data.stream()
                .map(UserRedDotVO::new)
                .toList());
    }

    @Override
    public ResponsePair<Void> clear(int category) {
        lambdaUpdate()
                .set(UserRedDot::getCount, 0)
                .eq(UserRedDot::getCategory, category)
                .eq(UserRedDot::getUid, RequestContext.getLoginUID());
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> increment(int category, int amount) {
        lambdaUpdate()
                .setIncrBy(UserRedDot::getCount, amount)
                .eq(UserRedDot::getCategory, category)
                .eq(UserRedDot::getUid, RequestContext.getLoginUID());
        return BusinessKit.ok();
    }
}
