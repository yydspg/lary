package cn.lary.external.wk.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceQuitDTO {

    private long uid;

    @JSONField(format="device_flag")
    private int deviceFlag;
}
