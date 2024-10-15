package cn.lary.module.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MemberAddDTO {

    @NotNull(message = "group id is null")
    private int groupId;

    @NotNull(message = "name is null")
    private String name;

    @NotNull(message = "members is null")
    private List<Integer> members; // group member uid
}

