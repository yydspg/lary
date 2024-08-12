package cn.lary.module.group.dto.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class CMDGroupAvatarUpdate {
    private String groupNo;
    private List<String> realUids;
}
