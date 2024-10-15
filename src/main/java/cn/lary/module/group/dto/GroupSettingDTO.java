package cn.lary.module.group.dto;

import lombok.Data;

@Data
public class GroupSettingDTO {
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

    /**
     * 群禁言
     */
    private Boolean isForbidden;

}
