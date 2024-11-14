package cn.lary.external.wk.dto.channel;

import cn.lary.external.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncMessageDTO extends WKChannel {
    @JSONField(format="login_uid")
    private String loginUID;
    @JSONField(format="start_message_seq")
    private long startMessageSeq;
    @JSONField(format="end_message_seq")
    private long endMessageSeq;
    private int limit;
    // 拉取模式 0 向下拉取，1 向上拉取
    @JSONField(format="pull_mode")
    private int pullMode;
}
