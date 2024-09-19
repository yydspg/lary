package cn.lary.module.group.dto;

import cn.lary.module.user.entity.UserBase;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MsgGroupCreate {
    private String creator;
    private String createName;
    private String groupNo;
    private long version;
    private List<UserBase> users;
}
