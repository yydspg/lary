package cn.lary.module.group.vo;

import cn.lary.module.group.entity.Group;
import lombok.Data;

@Data
public class GroupDetailVO {
    
    private Integer groupId;

    /**
     * 群名字
     */
    private String name;

    /**
     * 群主uid
     */
    private Integer creator;

    /**
     * 群状态
     */
    private Integer status;

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
    private Integer groupNum;

    /**
     * 群类型 0. 普通群，1.超大群
     */
    private Integer groupType;

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
    
    public GroupDetailVO of(Group group) {
        GroupDetailVO vo = new GroupDetailVO();
        vo.setGroupId(group.getGroupId());
        vo.setName(group.getName());
        vo.setCreator(group.getCreator());
        vo.setStatus(group.getStatus());
        vo.setIsForbidden(group.getIsForbidden());
        vo.setGroupAvatar(group.getGroupAvatar());
        vo.setGroupNum(group.getGroupNum());
        vo.setGroupType(group.getGroupType());
        vo.setCategory(group.getCategory());
        vo.setIsEnableInvite(group.getIsEnableInvite());
        vo.setIsForbiddenAddFriend(group.getIsForbiddenAddFriend());
        vo.setIsAllowViewHistoryMsg(group.getIsAllowViewHistoryMsg());
        return vo;
    }
}
