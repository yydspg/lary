package cn.lary.message.external.wk.dto.channel;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class SyncMessageDTO  {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

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
