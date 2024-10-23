package cn.lary.module.group.dto;

import cn.lary.module.user.vo.UserBaseVO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MsgGroupMemberAdd {
    private String groupNo;
    private String operator;
    private List<UserBaseVO> members;
}
