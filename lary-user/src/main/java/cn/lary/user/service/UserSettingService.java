package cn.lary.user.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.user.dto.UserSettingUpdateDTO;
import cn.lary.api.user.entity.UserSetting;
import cn.lary.api.user.vo.UserSettingVO;
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

    UserSetting build(UserSetting dto);

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
