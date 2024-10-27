package cn.lary.external.wk.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceQuitDTO {

    private long uid;

    @JsonProperty("device_flag")
    private int deviceFlag;
}
