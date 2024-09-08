package cn.lary.module.user.service;

import cn.lary.module.user.dto.res.FriendCodeCheck;
import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.dto.res.UserBasicInfo;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserShowInfo;
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

    User queryByName(String username);
    User queryByUID(String uid);
    UserBaseRes queryBase(String uid);
    UserBasicInfo queryUserBasicInfo(String uid);
    List<UserShowInfo> queryUserShowInfo(List<String> uids);
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
    List<UserBaseRes> queryUserBaseByUIDs(List<String> uids);

    FriendCodeCheck checkByCode(String code);
    FriendCodeCheck checkByQRCode(String code);
}
