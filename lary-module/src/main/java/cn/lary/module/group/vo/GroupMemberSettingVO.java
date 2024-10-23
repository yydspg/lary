package cn.lary.module.group.vo;

import cn.lary.module.group.entity.GroupMemberSetting;
import lombok.Data;

@Data
public class GroupMemberSettingVO {
    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否免打扰
     */
    private Boolean isNoDisturb;


    /**
     * 是否隐藏会话
     */
    private Boolean isHidden;

    /**
     * 是否开启进群提醒
     */
    private Boolean isJoinGroupRemind;

    /**
     * 是否显示群成员昵称
     */
    private Boolean isShowNickname;

    public GroupMemberSettingVO of(GroupMemberSetting setting) {
        GroupMemberSettingVO vo = new GroupMemberSettingVO();
        vo.setIsTop(setting.getIsTop());
        vo.setIsNoDisturb(setting.getIsNoDisturb());
        vo.setIsHidden(setting.getIsHidden());
        vo.setIsJoinGroupRemind(setting.getIsJoinGroupRemind());
        vo.setIsShowNickname(setting.getIsShowNickname());
        return vo;
    }
}
