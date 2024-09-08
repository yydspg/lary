package cn.lary.module.group.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GroupCreate {
    private String name; // group name
    @NotNull
    private List<String> members; // group member uid
}
