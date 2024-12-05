package cn.lary.api.stream.vo;

import cn.lary.api.stream.entity.Follow;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FollowVO {
    /**
     * uid
     */
    private long uid;

    /**
     * 被关注id
     */
    private long toUid;

    /**
     * 被关注用户名
     */
    private String username;
    /**
     * 业务来源
     */
    private int source;

    /**
     * 用户简介
     */
    private String bio;

    /**
     * 备注
     */
    private String remark;
    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 被关注角色
     */
    private int role;

    /**
     * 关注的状态 ,1 正常 2.取消关注 3.拉黑
     */
    private int status;

    /**
     * 粉丝等级
     */
    private int level;

    /**
     * 对主播的花费
     */
    private BigDecimal amount;

    private long createAt;

    public FollowVO() {}

    public FollowVO(Follow follow) {
        this.uid = follow.getUid();
        this.toUid = follow.getToUid();
        this.username = follow.getUsername();
        this.source = follow.getSource();
        this.bio = follow.getBio();
        this.remark = follow.getRemark();
        this.avatar = follow.getAvatar();
        this.role = follow.getRole();
        this.status = follow.getStatus();
        this.level = follow.getLevel();
        this.amount = follow.getAmount();
        this.createAt = follow.getCreateAt();
    }
}
