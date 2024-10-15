package cn.lary.pkg.wk.dto.channel;

import cn.lary.pkg.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncMessageDTO extends WKChannel {
    @JsonProperty("login_uid")
    private String loginUID;
    @JsonProperty("start_message_seq")
    private long startMessageSeq;
    @JsonProperty("end_message_seq")
    private long endMessageSeq;
    private int limit;
    // 拉取模式 0 向下拉取，1 向上拉取
    @JsonProperty("pull_mode")
    private int pullMode;
}
