package cn.lary.module.user.dto;

import cn.lary.core.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeviceLoginDTO  extends DTO {
    @JsonProperty("device_id")
    private String deviceId;
    @JsonProperty("device_name")
    private String deviceName;
    @JsonProperty("device_model")
    private String deviceModel;
    @JsonProperty("device_flag")
    @NotNull(message = "device flag not be null")
    private Integer deviceFlag;

    private Integer deviceLevel;
}
