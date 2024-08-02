package cn.lary.pkg.wk.entity.Response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnlineStatus {
    private String uid;
    @JsonProperty("device_flag")
    private byte deviceFlag;
    private int online;
}
