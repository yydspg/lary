package cn.lary.module.user.service;

import cn.lary.module.user.dto.res.FriendCodeCheck;
import cn.lary.module.user.entity.Friend;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface FriendService extends IService<Friend> {
    boolean isFriend(String uid,String toUid);
    void addSystemFriend(String uid);
    void addFileHelper(String uid);
    Friend get(String uid,String toUid);
    FriendCodeCheck checkByCode(String code);
}
