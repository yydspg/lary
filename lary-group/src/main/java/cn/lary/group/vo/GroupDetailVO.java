package cn.lary.group.vo;

import cn.lary.group.entity.Group;
import lombok.Data;

@Data
public class GroupDetailVO {
    
    private long gid;

    /**
     * 群名字
     */
    private String name;

    /**
     * 群主uid
     */
    private long creator;

    /**
     * 群状态
     */
    private int status;

    /**
     * 群禁言
     */
    private Boolean isForbidden;

    /**
     * 群头像路径
     */
    private String groupAvatar;

    /**
     * 群人数
     */
    private int groupNum;

    /**
     * 群类型 0. 普通群，1.超大群
     */
    private int groupType;

    /**
     * 群分类
     */
    private int category;

    /**
     * 群邀请开关
     */
    private Boolean isEnableInvite;

    /**
     * 是否禁止群内加好友
     */
    private Boolean isForbiddenAddFriend;

    /**
     * 是否允许新成员查看历史消息
     */
    private Boolean isAllowViewHistoryMsg;
    
    public GroupDetailVO(Group group) {
        setGid(group.getGid());
        setName(group.getName());
        setCreator(group.getCreator());
        setStatus(group.getStatus());
        setIsForbidden(group.getIsForbidden());
        setGroupAvatar(group.getGroupAvatar());
        setGroupNum(group.getGroupNum());
        setGroupType(group.getGroupType());
        setCategory(group.getCategory());
        setIsEnableInvite(group.getIsEnableInvite());
        setIsForbiddenAddFriend(group.getIsForbiddenAddFriend());
        setIsAllowViewHistoryMsg(group.getIsAllowViewHistoryMsg());

    }
}
