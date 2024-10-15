package cn.lary.module.group.dto;

import cn.lary.kit.StringKit;
import cn.lary.module.group.entity.GroupMemberSetting;
import lombok.Data;

@Data
public class GroupMemberSettingDTO {
    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否免打扰
     */
    private Boolean isNoDisturb;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 群聊备注
     */
    private String remark;

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

   public GroupMemberSetting to(){
       GroupMemberSetting setting = new GroupMemberSetting();
       if (StringKit.isNotEmpty(this.nickname)) {
           setting.setNickname(this.nickname);
       }
       if (StringKit.isNotEmpty(this.remark)) {
           setting.setRemark(this.remark);
       }
       if (this.isShowNickname != null) {
           setting.setIsShowNickname(this.isShowNickname);
       }
       if (this.isJoinGroupRemind != null) {
           setting.setIsJoinGroupRemind(this.isJoinGroupRemind);
       }
       if (this.isTop != null) {
           setting.setIsTop(this.isTop);
       }
       return setting;
   }
}
