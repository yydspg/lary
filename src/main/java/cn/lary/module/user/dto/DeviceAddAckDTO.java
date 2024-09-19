package cn.lary.module.user.dto;

import cn.lary.core.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeviceAddAckDTO extends DTO {
    @JsonProperty("device_name")
    private String deviceName;
    @JsonProperty("device_model")
    private String deviceModel;
    @NotNull
    private String verifyCode;
}
