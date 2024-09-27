package cn.lary.module.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupDTO {
    private String name; // group name
    @NotNull
    private List<Integer> members; // group member uid
}
