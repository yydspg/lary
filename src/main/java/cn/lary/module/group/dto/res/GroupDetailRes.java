package cn.lary.module.group.dto.res;

import cn.lary.core.dto.DTO;
import cn.lary.module.group.entity.GroupDetail;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupDetailRes extends DTO {
    // 群编号
    private String groupNo;
    // 群名
    private String name;
    // 群状态
    private Short status;

    /**
     * 群禁言
     */
    private boolean forbidden;

    /**
     * 群头像路径
     */
    private String groupAvatarUrl;

    /**
     * 群头像是否已经被上传
     */
    private Boolean isUploadAvatar;

    /**
     * 群类型 0. 普通群，1.超大群
     */
    private Short groupType;

    /**
     * 群分类
     */
    private String category;

    /**
     * 群邀请开关
     */
    private boolean invite;

    private boolean forbiddenAddFriend; //群内禁止加好友

    private boolean allowViewHistoryMsg; // 是否允许新成员查看历史消息

    private Long version; // 群数据版本

    private String notice;  // 群公告

    private String remark; // 群备注

    private Boolean mute;  // 免打扰

    private Boolean top; // 置顶

    private Boolean showNick; // 显示昵称

    private Boolean save; // 是否保存

    private Boolean chatPwdOn; //是否开启聊天密码

    private Boolean revokeRemind;  //撤回提醒

    private Boolean joinGroupRemind; //进群提醒

    private Boolean screenshot; //截屏通知

    private Boolean receipt; //消息是否回执

    private LocalDateTime createAt;


    private LocalDateTime updateAt;
    public static GroupDetailRes of(GroupDetail group) {
        GroupDetailRes g = new GroupDetailRes();
        g.setGroupNo(group.getGroup().getGroupNo());
        g.setName(group.getGroup().getName());
        g.setStatus(group.getGroup().getStatus());
        g.setForbidden(group.getGroup().isForbidden());
        g.setGroupAvatarUrl(group.getGroup().getGroupAvatarUrl());
        g.setSave(group.getGroupSetting().getSave());
        g.setChatPwdOn(group.getGroupSetting().getChatPwdOn());
        g.setGroupType(group.getGroup().getGroupType());
        g.setCategory(group.getGroup().getCategory());
        g.setInvite(group.getGroup().isInvite());
        g.setForbiddenAddFriend(group.getGroup().isForbiddenAddFriend());
        g.setAllowViewHistoryMsg(group.getGroup().isAllowViewHistoryMsg());
        g.setGroupType(group.getGroup().getGroupType());
        g.setCreateAt(group.getGroup().getCreateAt());
        g.setUpdateAt(group.getGroup().getUpdateAt());
        g.setReceipt(group.getGroupSetting().getReceipt());
        g.setScreenshot(group.getGroupSetting().getScreenshot());
        return g;
    }
}
