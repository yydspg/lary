package cn.lary.module.user.dto;

import cn.lary.core.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeviceLoginDTO  extends DTO {
    @JsonProperty("id")
    private String id;

    @NotNull(message = "device_name is null")
    @JsonProperty("name")
    private String name;

    @JsonProperty("model")
    private String model;

    @JsonProperty("flag")
    @NotNull(message = "device flag is null")
    private Integer flag;

    private Integer level;
}
