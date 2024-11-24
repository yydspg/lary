package cn.lary.user.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.user.entity.UserRedDot;
import cn.lary.user.mapper.UserRedDotMapper;
import cn.lary.user.service.UserRedDotService;
import cn.lary.user.vo.UserRedDotVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserRedDotServiceImpl extends ServiceImpl<UserRedDotMapper, UserRedDot> implements UserRedDotService {

    private final LaryIDBuilder builder;

    @Override
    public UserRedDot build(UserRedDot dto) {
        dto.setRid(builder.next());
        save(dto);
        return dto;
    }

    @Override
    public ResponsePair<List<UserRedDotVO>> redDots() {
        List<UserRedDot> data = lambdaQuery()
                .select(UserRedDot::getCategory, UserRedDot::getCount,
                        UserRedDot::getIsDot)
                .eq(UserRedDot::getUid, RequestContext.uid())
                .orderByDesc(UserRedDot::getCreateAt)
                .list();
        if (CollectionKit.isEmpty(data)) {
            log.error("search red dots failed,uid:{}",RequestContext.uid());
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
                .eq(UserRedDot::getUid, RequestContext.uid())
                .update();
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> increment(int category, int amount) {
        lambdaUpdate()
                .setIncrBy(UserRedDot::getCount, amount)
                .eq(UserRedDot::getCategory, category)
                .eq(UserRedDot::getUid, RequestContext.uid())
                .update();
        return BusinessKit.ok();
    }
}
