package cn.lary.module.event.dto;

import cn.lary.kit.JSONKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.event.convert.EventConvert;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoLiveEventDTO implements EventConvert {

    private Integer uid;

    @JsonProperty("device_id")
    private int deviceId;

    @JsonProperty("stream_id")
    private int streamId;

    @JsonProperty("wk_channel_id")
    private int wkChannelId;

    @Override
    public EventData of() {
         return new EventData()
                 .setEvent(Lary.Event.goLive)
                 .setType(Lary.EventType.cmd)
                 .setData(JSONKit.toJSON(this));
    }
}
