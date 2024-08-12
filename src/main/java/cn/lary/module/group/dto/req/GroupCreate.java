package cn.lary.module.group.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GroupCreate {
    @NotNull(message = "creator is null")
    private String uid;
    @JsonProperty("create_name")
    @NotNull(message = "create name is null")
    private String createName;
    private String name; // group name
    private List<String> members; // group member uid
}
