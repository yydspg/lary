package cn.lary.module.user.service;

import cn.lary.module.user.vo.FriendCodeCheck;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.user.vo.UserBasicInfo;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserShowInfo;
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
    UserBaseVO queryBase(String uid);
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
    List<UserBaseVO> queryUserBaseByUIDs(List<String> uids);

    FriendCodeCheck checkByCode(String code);
    FriendCodeCheck checkByQRCode(String code);
}
