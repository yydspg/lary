package cn.lary.external.wk.dto.channel;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public  class WKChannelCreateDTO {

    @JSONField(format = "channel_id")
    private long cid;

    @JSONField(format = "channel_type")
    private int type;

    private int large;

    private int ban;

    private List<Long> subscribers;
 }
