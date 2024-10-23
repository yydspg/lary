package cn.lary.external.wk.vo.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnlineStatus {
    private Integer uid;
    @JsonProperty("device_flag")
    private byte deviceFlag;
    private int online;
}
