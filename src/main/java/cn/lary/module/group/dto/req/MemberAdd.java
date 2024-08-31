package cn.lary.module.group.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MemberAdd {
    @NotNull(message = "uid is null")
    private String uid;
    @NotNull(message = "name is null")
    private String name;
    @NotNull(message = "members is null")
    private List<String> members; // group member uid
}

