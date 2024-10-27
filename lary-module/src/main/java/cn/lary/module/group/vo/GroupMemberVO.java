package cn.lary.module.group.vo;

import cn.lary.module.group.entity.GroupMember;
import lombok.Data;

@Data
public class GroupMemberVO {

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 本群昵称
     */
    private String nickname;

    /**
     * 群聊角色
     */
    private Integer role;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 是否是机器人
     */
    private Boolean isRobot;
    /**
     * 群成员头像
     */
    private String avatar;

    /**
     * 群成员用户名
     */
    private String name;
    
    public GroupMemberVO of(GroupMember groupMember) {
        GroupMemberVO vo = new GroupMemberVO();
        vo.setUid(groupMember.getUid());
        vo.setNickname(groupMember.getNickname());
        vo.setRole(groupMember.getRole());
        vo.setVerifyCode(groupMember.getVerifyCode());
        vo.setIsRobot(groupMember.getIsRobot());
        vo.setAvatar(groupMember.getAvatar());
        vo.setName(groupMember.getName());
        return vo;
    }

}
