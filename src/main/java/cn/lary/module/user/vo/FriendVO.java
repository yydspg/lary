package cn.lary.module.user.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FriendVO {
    /**
     * 用户UID
     */
    private String uid;

    /**
     * 好友uid
     */
    private String toUid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 是否是特别关注
     */
    private String isSuper;
    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 备注
     */
    private String remark;
    /**
     * 个人介绍
     */
    private String bio;
}
