package cn.lary.module.group.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateGroupVO {
    private int groupId;
    private String groupName;
    private LocalDateTime createAt;
}
