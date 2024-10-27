package cn.lary.module.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupDTO {
    @NotNull
    private Integer category;

    private String name; // group name
    @NotNull
    private List<Long> members; // group member uid
}
