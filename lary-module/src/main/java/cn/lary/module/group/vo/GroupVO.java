package cn.lary.module.group.vo;

import cn.lary.module.group.entity.Group;
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
    private Long id;

    private Integer num;

    public GroupVO of(Group group) {
        GroupVO vo = new GroupVO();
        vo.name = group.getName();
        vo.avatar = group.getGroupAvatar();
        vo.id = group.getGroupId();
        vo.num = group.getGroupNum();
        return vo;
    }

}
