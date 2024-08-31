package cn.lary.pkg.wk.entity.Request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceQuitReq {
    private String uid;
    @JsonProperty("device_flag")
    private byte deviceFlag;
}
