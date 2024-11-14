package cn.lary.external.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageHeader {
    @JSONField(format="red_dot")
    private int redDot;
    @JSONField(format="sync_once")
    private int syncOnce;
    @JSONField(format="no_persist")
    private int noPersist;
}
