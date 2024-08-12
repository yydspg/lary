package cn.lary.module.group.dto.req;

import cn.lary.module.user.dto.res.UserBaseRes;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MsgGroupMemberAdd {
    private String groupNo;
    private String operator;
    private List<UserBaseRes> members;
}
