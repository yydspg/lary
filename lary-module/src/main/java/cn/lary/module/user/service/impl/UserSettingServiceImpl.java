package cn.lary.module.user.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.id.LaryIDBuilder;
import cn.lary.module.user.dto.UserSettingUpdateDTO;
import cn.lary.module.user.entity.UserSetting;
import cn.lary.module.user.mapper.UserSettingMapper;
import cn.lary.module.user.service.UserSettingService;
import cn.lary.module.user.vo.UserSettingVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements UserSettingService {

    private final LaryIDBuilder builder;

    @Override
    public UserSetting build(UserSetting dto) {
        dto.setSid(builder.next());
        save(dto);
        return dto;
    }
    //todo impl
    @Override
    public ResponsePair<UserSettingVO> update(UserSettingUpdateDTO dto) {
        return null;
    }

    @Override
    public ResponsePair<UserSettingVO> get() {
        UserSetting setting = lambdaQuery()
                .select(UserSetting::getMedal,UserSetting::getDynamic,
                        UserSetting::getFanList,UserSetting::getNewMessageNotice)
                .eq(UserSetting::getUid, RequestContext.uid())
                .one();
        if (setting == null) {
            log.error("search user setting failed,uid:{}",RequestContext.uid());
            return BusinessKit.fail("user setting not found");
        }
        return BusinessKit.ok(new UserSettingVO(setting));
    }
}
