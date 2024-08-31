package cn.lary.module.user.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceRegisterTokenReq {
    @JsonProperty("device_token")
    @NotNull(message = "device token is null")
    private String deviceToken;
    @NotNull(message = "device type is null")
    @JsonProperty("device_type")
    private String deviceType;
    @NotNull(message = "bundle id is null")
    @JsonProperty("bundle_id")
    private String bundleId;
}
