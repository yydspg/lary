package cn.lary.module.stream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FollowDTO {
    @JsonProperty("to_uid")
    @NotNull
    private Integer toUid;
    @NotNull
    private Integer code;
}
