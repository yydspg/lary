package cn.lary.pkg.wk.entity.Request.channel;

import cn.lary.pkg.wk.entity.core.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncMessageReq extends Channel {
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
