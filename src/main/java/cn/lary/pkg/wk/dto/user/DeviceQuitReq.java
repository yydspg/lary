package cn.lary.pkg.wk.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceQuitReq {
    private Integer uid;
    @JsonProperty("device_flag")
    private byte deviceFlag;
}
