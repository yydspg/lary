package cn.lary.module.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupDTO {
    // TODO  :  此处处理好 category 的校验
    private Integer category;
    private String name; // group name
    @NotNull
    private List<Integer> members; // group member uid
}
