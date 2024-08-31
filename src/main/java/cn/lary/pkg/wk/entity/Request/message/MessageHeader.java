package cn.lary.pkg.wk.entity.Request.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageHeader {
    @JsonProperty("red_dot")
    private int redDot;
    @JsonProperty("sync_once")
    private int syncOnce;
    @JsonProperty("no_persist")
    private int noPersist;
}
