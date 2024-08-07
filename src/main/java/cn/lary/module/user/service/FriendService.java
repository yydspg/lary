package cn.lary.module.user.service;

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
    public boolean isFriend(String uid,String toUid);
    public void addSystemFriend(String uid);
    public void addFileHelper(String uid);
}
