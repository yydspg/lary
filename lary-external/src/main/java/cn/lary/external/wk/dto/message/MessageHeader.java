package cn.lary.external.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageHeader {
    @JsonProperty("red_dot")
    private int redDot;
    @JsonProperty("sync_once")
    private int syncOnce;
    @JsonProperty("no_persist")
    private int noPersist;
}
