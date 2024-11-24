package cn.lary.group.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MsgGroupMemberAdd {
    private String groupNo;
    private String operator;
//    private List<UserBaseVO> members;
}
