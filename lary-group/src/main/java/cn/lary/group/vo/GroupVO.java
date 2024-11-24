package cn.lary.group.vo;

import cn.lary.group.entity.Group;
import lombok.Data;

@Data
public class GroupVO {

    /**
     * 群名字
     */
    private String name;


    /**
     * 群头像路径
     */
    private String avatar;

    /**
     * 群Id
     */
    private Long gid;

    private Integer num;

    public GroupVO(Group group) {
        name = group.getName();
        avatar = group.getGroupAvatar();
        gid = group.getGid();
        num = group.getGroupNum();
    }

}
