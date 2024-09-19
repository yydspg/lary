package cn.lary.module.group.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MsgGroupAvatarUpdate {
    private String groupNo;
    private List<String> members;
}
