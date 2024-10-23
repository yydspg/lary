package cn.lary.module.user.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FriendBase {
    /**
     * 用户UID
     */
    private Integer uid;

    /**
     * 好友uid
     */
    private Integer toUid;

    /**
     * 好友标示
     */
    private Short flag;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 验证码 加好友来源
     */
    private String vercode;

    /**
     * 好友来源
     */
    private String sourceVercode;

    /**
     * 是否已删除
     */
    private Boolean isDeleted;

    /**
     * 单项好友
     */
    private Short isAlone;

    /**
     * 加好友发起方
     */
    private Short initiator;
}
