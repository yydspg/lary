package cn.lary.module.user.service;

import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.entity.User;
import com.alipay.api.domain.UserBaseInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface UserService extends IService<User> {
    // 存在时
    User queryByUsername(String username);
    User queryByUID(String uid);

    /**
     * use this api carefully
     * @param uids user id list
     * @return useful user list
     */
    List<User> queryByUIDs(List<String> uids);

    /**
     *
     * @param uids user id list
     * @return @UserbaseRes
     */
    List<UserBaseRes> queryUserBaseInfoByUIDs(List<String> uids);
}
