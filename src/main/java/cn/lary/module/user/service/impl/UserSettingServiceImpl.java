package cn.lary.module.user.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.module.user.dto.UserSettingUpdateDTO;
import cn.lary.module.user.entity.UserSetting;
import cn.lary.module.user.mapper.UserSettingMapper;
import cn.lary.module.user.service.UserSettingService;
import cn.lary.module.user.vo.UserSettingVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements UserSettingService {

    @Override
    public ResponsePair<UserSettingVO> update(UserSettingUpdateDTO dto) {
        return null;
    }

    @Override
    public ResponsePair<UserSettingVO> get() {
        UserSetting setting = lambdaQuery()
                .eq(UserSetting::getUid, RequestContext.getLoginUID())
                .one();
        if (setting == null) {
            log.error("search user setting failed,uid:{}",RequestContext.getLoginUID());
            return BizKit.fail("user setting not found");
        }
        return BizKit.ok(new UserSettingVO(setting));
    }
}
