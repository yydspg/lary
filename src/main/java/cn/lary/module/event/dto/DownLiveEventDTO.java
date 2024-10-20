package cn.lary.module.event.dto;

import cn.lary.kit.JSONKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.convert.EventConvert;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownLiveEventDTO implements EventConvert {
    private int uid;
    @JsonProperty("stream_id")
    private int streamId;
    @JsonProperty("channel_id")
    private int channelId;

    @Override
    public EventData of() {
        return new EventData()
                .setEvent(LARY.Event.downLive)
                .setType(LARY.EventType.cmd)
                .setEvent(JSONKit.toJSON(this));
    }
}
