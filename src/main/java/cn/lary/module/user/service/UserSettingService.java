package cn.lary.module.user.service;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.user.dto.UserSettingUpdateDTO;
import cn.lary.module.user.entity.UserSetting;
import cn.lary.module.user.vo.UserSettingVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface UserSettingService extends IService<UserSetting> {

    /**
     * 更新用户设置
     * @param dto {@link UserSettingUpdateDTO}
     * @return {@link UserSettingVO}
     */
    ResponsePair<UserSettingVO> update(UserSettingUpdateDTO dto);

    /**
     * 查询用户设置
     * @return {@link UserSettingVO}
     */
    ResponsePair<UserSettingVO> get();
}
