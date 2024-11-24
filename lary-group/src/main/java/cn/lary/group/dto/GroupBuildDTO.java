package cn.lary.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GroupBuildDTO {
    @NotNull
    private Integer category;

    private String name; // group name
    @NotNull
    private List<Long> members; // group member uid
}
